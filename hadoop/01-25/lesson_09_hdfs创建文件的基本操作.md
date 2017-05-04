需求: 从HDFS中读取文本文件内容
参考<Hadoop权威指南>
```
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

public class URLCat {
	static{
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		InputStream in = null;
		try {
			in = new URL(args[0]).openStream();
			IOUtils.copyBytes(in, System.out, 4096, false);
		}finally{
			// TODO: handle exception
			IOUtils.closeStream(in);
		}
	}
}
```
然后打包, 包名为 hadoop-tools.jar
```
hadoop jar hadoop-tools.jar URLCat  hdfs://localhost:9000/user/shgy/output/part-r-00000
```

代码分析: 关键代码在与:
```
static{
    URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
}
```


需求: 将本地文件写入HDFS中.
参考 Hadoop in Action

```
package hdfs.remote;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/*
 * 在本机启动一个单机伪集群, 进行hdfs的操作, 相比CLI_MiniCluster, 每次处理后,数据是保存好的.
 * 可以使用命令来查看数据.
 *  hdfs dfs -mkdir /user
 * */
public class PutMerge {
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://localhost:9000");
		FileSystem hdfs = FileSystem.get(conf);

		System.out.println("home dir is "+hdfs.getHomeDirectory().getName());

		System.out.println("=====================hdfs file========================");
		FileStatus[] status = hdfs.listStatus(new Path("/user"));
		for (FileStatus f:status) {
			System.out.println(f.getPath().getName());
		}

		System.out.println("=====================local file========================");
		FileSystem local = FileSystem.getLocal(conf);
		status = local.listStatus(new Path("/home/shgy/tmp"));
		for (FileStatus f:status) {
			System.out.println(f.getPath().getName());
		}

		/*
		 * copy local to hdfs
		 * 这是偷懒的做法
		 * */
		hdfs.copyFromLocalFile(new Path("file:///home/shgy/tmp/bb.txt"), new Path("/user/bb.txt"));
		/*
		 * 创建文件, 如果是相对路径, 则使用/user/shgy/目录, 因为/user/shgy是HomeDirectory
		 *
		 * 如果hdfs中文件存在, 则覆盖原来的文件
		 * */
		try {
			FSDataOutputStream out = hdfs.create(new Path("cc.txt"));

			FSDataInputStream in = local.open(new Path("file:///home/shgy/tmp/bb.txt"));
			byte buffer[] = new byte[4096];
			int bytesRead = 0;
			while((bytesRead = in.read(buffer)) > 0){
				out.write(buffer);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```

hadoop的hdfs在太多的地方借鉴和参考了Linux的文件系统, 除了它是分布式文件系统这一点.

