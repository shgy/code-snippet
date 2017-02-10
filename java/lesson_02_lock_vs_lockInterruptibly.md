Java的Lock对象有两个方法`lock()`和`lockInterruptibly()` . 这两个方法有什么区别呢?
其实, 通过名字就可以区分, `lockInterruptibly()`在等待锁的过程中可以中断. 如何理解呢?
```
package jdk.fundations.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantLockDemo {
	
	static class FileSystem{
		private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		
		public void read() throws InterruptedException{
			try {
				lock.readLock().lock();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("read from filesystem");
				
			} finally {
				// TODO: handle finally clause
				lock.readLock().unlock();
			}
		}
		
		public void write() throws InterruptedException{
			try {
				lock.writeLock().lock();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("write");
			} finally {
				// TODO: handle finally clause
				lock.writeLock().unlock();
			}
		}
	}
	
	
	static class Client implements Runnable{
		private FileSystem fs;
		
		public Client(FileSystem fs){
			this.fs = fs;
		}
		
		
		public void run(){
			while(true){
				
				try {
					this.fs.write();
					this.fs.read();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		 FileSystem fs = new FileSystem();
		 
		 Thread[] clients = new Thread[10];
		 
		 for(int i=0;i<clients.length;i++){
			 clients[i] = new Thread(new Client(fs));;
		 }
		 
		 for(Thread c: clients){
			 c.start();
		 }
		 
		 clients[5].interrupt();
	}
	
}

```
把代码中的`lock.writeLock().lock();`替换成`lock.writeLock().lockInterruptibly();`  对比两者的运行就能感受到两者不同了.

如果再深入一步, 两者是如何实现的呢?



