搜索服务器Solr作为webapp存在，通过web服务器，如Tomcat、Jetty等与客户端进行通信。这也使得Solr的学习成本比较高，在学习Solr之前，至少需要学习Java Web编程。而ElasticSearch通过elasticsearh.bat直接启动就可以用了，安装和使用成本非常低。而且ES可以直接用浏览器访问，ES中内置了Http服务器，它就是Netty。
Netty是一个异步的、事件驱动的网络应用框架。用它可以快速开发高性能、高可靠性的网络服务器和客户端程序。本文以一个简单的C/S通信为例，来快速了解Netty。
创建maven项目NettyDemo，pom.xml中引入Netty的jar包：
```
 <dependencies>

 	<dependency>

<groupId>io.netty</groupId>

<artifactId>netty</artifactId>

<version>3.5.6.Final</version>

</dependency>

 </dependencies>

```
创建服务端:NettyServer.java
```
package server;



import java.net.InetSocketAddress;

import java.util.concurrent.Executors;



import org.jboss.netty.bootstrap.ServerBootstrap;

import org.jboss.netty.channel.Channel;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;



import common.MyChannelHandler;



public class NettyServer {

final static int port = 8080;  

ServerBootstrap bootstrap;

   Channel parentChannel;

   InetSocketAddress localAddress = new InetSocketAddress(port);

   MyChannelHandler channelHandler = new MyChannelHandler("Server");

   public static void main(String[] args) {  

   	NettyServer server = new NettyServer();  

   	server.init();

       server.start();

   }

private void init() {  

       bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(  

              Executors.newCachedThreadPool(), Executors  

                      .newCachedThreadPool()));

       bootstrap.setOption("reuseAddress", true);

       bootstrap.setOption("child.tcpNoDelay", true);

       bootstrap.setOption("child.soLinger", 2);  

      bootstrap.getPipeline().addLast("servercnfactory", channelHandler);

   }

private void start() {  

      parentChannel = bootstrap.bind(localAddress);

}

}

```
创建客户端：
```
package client;



import java.net.InetSocketAddress;

import java.util.concurrent.Executors;



import org.jboss.netty.bootstrap.ClientBootstrap;

import org.jboss.netty.channel.ChannelHandler;

import org.jboss.netty.channel.ChannelPipeline;

import org.jboss.netty.channel.ChannelPipelineFactory;

import org.jboss.netty.channel.Channels;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;



import common.MyChannelHandler;



public class NettyClient {

final static String host = "127.0.0.1";

final static int port = 8080;

ClientBootstrap bootstrap;

ChannelHandler myHandler = new MyChannelHandler("Client");

public static void main(String[] args) {

NettyClient client = new NettyClient();

client.init();

client.start();

}

void init() {

bootstrap = new ClientBootstrap(

          new NioClientSocketChannelFactory(

                  Executors.newCachedThreadPool(),

                  Executors.newCachedThreadPool()));

bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

       public ChannelPipeline getPipeline() throws Exception {

           return Channels.pipeline(myHandler);

       }

   });

bootstrap.setOption("remoteAddress", new InetSocketAddress(host, port));



}

void start() {

bootstrap.connect();

}

}

```
创建一个处理器：MyChannelHandler.java
```
package common;



import java.util.Scanner;



import org.jboss.netty.buffer.ChannelBuffer;

import org.jboss.netty.buffer.ChannelBuffers;

import org.jboss.netty.channel.Channel;

import org.jboss.netty.channel.ChannelHandlerContext;

import org.jboss.netty.channel.ChannelStateEvent;

import org.jboss.netty.channel.MessageEvent;

import org.jboss.netty.channel.SimpleChannelHandler;



public class MyChannelHandler extends SimpleChannelHandler {

	private String name;

	public MyChannelHandler(String name){

		this.name = name;

	}

	

	@Override

	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)

			throws Exception {

		// TODO Auto-generated method stub

		System.out.println("Channel closed " + e);

	}



	@Override

	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)

			throws Exception {

		// TODO Auto-generated method stub

		System.out.println("Channel connected " + e);

		new Thread(new MsgSender(e.getChannel())).start();

	}



	@Override

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)

			throws Exception {

		try {

//			System.out.println("New message " + e.toString() + " from "

//					+ ctx.getChannel());

			ChannelBuffer buf = (ChannelBuffer) e.getMessage();

			byte[] bytes = buf.array();

			System.out

					.println(name+" reseived message : " + new String(bytes));

		} catch (Exception ex) {

			ex.printStackTrace();

			throw ex;

		}

	}

	

	class MsgSender implements Runnable{

		private Channel c ;

		private Scanner s = new Scanner(System.in);

		public MsgSender(Channel c){

			this.c = c;

		}

		@Override

		public void run() {

			while(true){

				String input = s.nextLine();

				System.out.println(name+" send	message : "+input);

		        c.write(ChannelBuffers.wrappedBuffer(input.getBytes()) );  

			}

		}

		

	}

}

```

处理方式如下：
一个内部类MsgSender，不停地等待输入并将输入信息发送出去。
收到消息就打印出来。
运行NettyServer和NettyClient，进行通信。




服务器也可以向Client发送信息：





这样就实现了一个简单的C/S通信程序。很容易发现，程序处理的核心在MyChannelHandler.java类。如果接收到信息，就会触发相关事件，messageReceived()方法就会被调用，这就是事件驱动。
一般了解到这里对了解ElasticSearch的源码也就够用了。但是Netty是一个很值得深入学习的框架，通过Netty源码的学习，会对网络编程有更深的理解。

