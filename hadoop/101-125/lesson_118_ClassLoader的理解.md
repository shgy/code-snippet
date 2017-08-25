Java 设计可以从本地文件/网络中加载class。 为了安全性， 设计了“双亲委派模型”： 
```
(1).如果一个类加载器收到了类加载请求，它首先不会自己去尝试加载这个类，而是把类加载请求委派给父类加载器去完成。
(2).每一层的类加载器都把类加载请求委派给父类加载器，直到所有的类加载请求都应该传递给顶层的启动类加载器。
(3).如果顶层的启动类加载器无法完成加载请求，子类加载器尝试去加载，如果连最初发起类加载请求的类加载器也无法完成加载请求时，将会抛出ClassNotFoundException，而不再调用其子类加载器去进行类加载。
双亲委派 模式的类加载机制的优点是java类它的类加载器一起具备了一种带优先级的层次关系，越是基础的类，越是被上层的类加载器进行加载，保证了java程序的稳定运行。
```

```
package shgy.guice;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class NetworkClassLoader extends ClassLoader {
   private String rootUrl;

   public NetworkClassLoader(String rootUrl) {
      super((ClassLoader)null);
      this.rootUrl = rootUrl;
   }

   protected Class<?> findClass(String name) throws ClassNotFoundException {
      Class clazz = null;
      byte[] classData = this.getClassData(name);
      if(classData == null) {
         throw new ClassNotFoundException();
      } else {
         clazz = this.defineClass(name, classData, 0, classData.length);
         return clazz;
      }
   }

   private byte[] getClassData(String name) {
      InputStream is = null;

      try {
         String e = this.classNameToPath(name);
         URL url = new URL(e);
         byte[] buff = new byte[4096];
         boolean len = true;
         is = url.openStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         int len1;
         while((len1 = is.read(buff)) != -1) {
            baos.write(buff, 0, len1);
         }

         byte[] var8 = baos.toByteArray();
         return var8;
      } catch (Exception var18) {
         var18.printStackTrace();
      } finally {
         if(is != null) {
            try {
               is.close();
            } catch (IOException var17) {
               var17.printStackTrace();
            }
         }

      }

      return null;
   }

   private String classNameToPath(String name) {
      return this.rootUrl + "/" + name.replace(".", "/") + ".class";
   }
}

public class ThreadDemo implements Runnable {

   Thread t;

   public ThreadDemo() {

      t = new Thread(this);
      t.setContextClassLoader(null);
      // this will call run() function
      t.start();
   }

   public void run() {

      ClassLoader c = t.getContextClassLoader();
      // sets the context ClassLoader for this Thread
      System.out.println(c);

   }

   public static void main(String args[]) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
     
      NetworkClassLoader loader1 = new NetworkClassLoader("file:///home/shgy/hadoop_workspace/hadoop/hadoop-yarn-project/shgy-demos/target/classes");
      NetworkClassLoader loader2 = new NetworkClassLoader("file:///home/shgy/hadoop_workspace/hadoop/hadoop-yarn-project/shgy-demos/target/classes");
      Class b = loader1.loadClass("shgy.guice.ThreadDemo");
      Class c =loader2.loadClass("shgy.guice.ThreadDemo");
      System.out.println(b.equals(c));
   }
} 
```

这样保证了一个JVM进程中， 同一个类只会有一个class对象。 但是对于web应用， 比如Tomcat/jetty,  在一个服务进程中会管理着多个web应用。
各个web应用之间要保持隔离， 不能相互影响。 因此， Tomcat/jetty 加载web应用采用自定义的ClassLoader, 每个webapp一个ClassLoader实例。


当使用new 关键词时， new会自动使用new关键字所在的类的类加载器来加载类。
