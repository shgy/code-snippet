Hadoop的DistributedShell和hadoop-streaming都有一个功能, 就是在Java程序中执行Shell命令.

Java lang包中执行shell命令的类.
```
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SimpleDemo {
	public static void main(String[] args) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("ls");

		Process p = builder.start();
		BufferedReader inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line=null;
		while((line = inReader.readLine())!=null){
			System.out.println(line);
		}

		InputStream stdout = p.getInputStream();
        synchronized (stdout) {
          inReader.close();
        }
	}
}
```

上面的demo可以说非常简单, 只是演示了ProcessBuilder的功能,  在真实的应用场景中, 如何写一个健壮的代码呢? Hadoop的编程套路可以
作为一个很好的参考.
它考虑了:
1. Shell脚本运行时间
2. 错误处理
3. 进程的终结 p.destory(), 避免僵尸进程.
这几种情况.
```

package shell.rundemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.Shell.ExitCodeException;

/*

 需求: 如何在Java中运行shell命令或者脚本, 然后读取返回结果

 需求背景: Hadoop提供了streaming功能, streaming可以调用shell命令或者python脚本执行map-reduce任务.

 */
public class RunShellDemo {
	public static final Log LOG = LogFactory.getLog(RunShellDemo.class);

	private static AtomicBoolean timedOut;
	private static AtomicBoolean completed;
    private static Process process; // sub process used to execute the command
	private static  int exitCode;
	private static long lastTime;   // last time the command was performed

	public static void main(String[] args) throws IOException {

		ProcessBuilder builder = new ProcessBuilder("ls");
	    Timer timeOutTimer = null;
	    ShellTimeoutTimerTask timeoutTimerTask = null;
	    timedOut = new AtomicBoolean(false);
	    completed = new AtomicBoolean(false);

		builder.directory(new File("/home/shgy/"));

		Process process = builder.start();

		  timeOutTimer = new Timer("Shell command timeout");
	      timeoutTimerTask = new ShellTimeoutTimerTask();
	      //One time scheduling.
	      timeOutTimer.schedule(timeoutTimerTask, 10*1000);

		final BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		final StringBuffer errMsg = new StringBuffer();

		// read error and input streams as this would free up the buffers
		// free the error stream buffer
		Thread errThread = new Thread() {
			@Override
			public void run() {
				try {
					String line = errReader.readLine();
					while ((line != null) && !isInterrupted()) {
						errMsg.append(line);
						errMsg.append(System.getProperty("line.separator"));
						line = errReader.readLine();
					}
				} catch (IOException ioe) {
					LOG.warn("Error reading the error stream", ioe);
				}
			}
		};
		try {
		      errThread.start();
		} catch (IllegalStateException ise) { }

		try {

			  StringBuffer buf = parseExecResult(inReader); // parse the output
			  System.out.println(buf.toString());

			  // clear the input stream buffer
		      String line = inReader.readLine();
		      while(line != null) {
		        line = inReader.readLine();
		      }
		      // wait for the process to finish and check the exit code
		      exitCode  = process.waitFor();
		      // make sure that the error thread exits
		      joinThread(errThread);
		      completed.set(true);
		      //the timeout thread handling
		      //taken care in finally block
		      if (exitCode != 0) {
		        throw new ExitCodeException(exitCode, errMsg.toString());
		      }
		    } catch (InterruptedException ie) {
		      throw new IOException(ie.toString());
		    } finally {
		      if (timeOutTimer != null) {
		        timeOutTimer.cancel();
		      }
		      // close the input stream
		      try {
		        // JDK 7 tries to automatically drain the input streams for us
		        // when the process exits, but since close is not synchronized,
		        // it creates a race if we close the stream first and the same
		        // fd is recycled.  the stream draining thread will attempt to
		        // drain that fd!!  it may block, OOM, or cause bizarre behavior
		        // see: https://bugs.openjdk.java.net/browse/JDK-8024521
		        //      issue is fixed in build 7u60
		        InputStream stdout = process.getInputStream();
		        synchronized (stdout) {
		          inReader.close();
		        }
		      } catch (IOException ioe) {
		        LOG.warn("Error while closing the input stream", ioe);
		      }
		      if (!completed.get()) {
		        errThread.interrupt();
		        joinThread(errThread);
		      }
		      try {
		        InputStream stderr = process.getErrorStream();
		        synchronized (stderr) {
		          errReader.close();
		        }
		      } catch (IOException ioe) {
		        LOG.warn("Error while closing the error stream", ioe);
		      }
		      process.destroy();
		      lastTime = Time.now();
		    }
	}

	public static Process getProcess(){
		return process;
	}

    private static void joinThread(Thread t) {
	    while (t.isAlive()) {
	      try {
	        t.join();
	      } catch (InterruptedException ie) {
	        if (LOG.isWarnEnabled()) {
	          LOG.warn("Interrupted while joining on: " + t, ie);
	        }
	        t.interrupt(); // propagate interrupt
	      }
	    }
	  }
	private static void setTimedOut() {
		    timedOut.set(true);
   }

    protected static StringBuffer parseExecResult(BufferedReader lines) throws IOException {
        StringBuffer output = new StringBuffer();
        char[] buf = new char[512];
        int nRead;
        while ( (nRead = lines.read(buf, 0, buf.length)) > 0 ) {
          output.append(buf, 0, nRead);
        }
        return output;
    }


    private static class ShellTimeoutTimerTask extends TimerTask {

        @Override
        public void run() {
          Process p = RunShellDemo.getProcess();
          try {
        	 p.exitValue();
          } catch (Exception e) {
            //Process has not terminated.
            //So check if it has completed
            //if not just destroy it.
            if (p != null && !completed.get()) {
              setTimedOut();
              p.destroy();
            }
          }
        }
      }
}

```