`以前学ES的时候写的笔记` 有点印象， 翻出来了。
ES入门基础之Guice框架

Guice是与Spring功能类似的轻量级依赖注入框架。Guice框架在2008年打败Spring拿到Jolt Award大奖。 Guice非常简洁，只有一个jar包，也不需要配置文件。
八卦一下……Guice是由Google公司的Bob Lee开发的，据说，他之所以自己重复造轮子，是因为他不喜欢现在IoC框架使用XML配置文件进行bean组装的方式，
因为这样无法把代码修改和配置文件修改同步，配置文件也无法进行类型检查。
下面通过一个Hello World的例子来学习Guice的工作方式。应用需求如下：一个App对外发送信息，发送目的地可以是Email，也可是社交网络，比如FaceBook。
在项目中引入Guice的jar包，maven依赖如下：
```
<dependency>
		<groupId>com.google.inject</groupId>
		<artifactId>guice</artifactId>
		<version>3.0</version>
</dependency>
```
首先定义程序的入口：MyApplication.java
```package com.journaldev.di.consumer;

import javax.inject.Inject;

//import com.google.inject.Inject;
import com.journaldev.di.services.MessageService;

public class MyApplication {

	private MessageService service;
	@Inject
	public MyApplication(MessageService svc){
		this.service = svc;
	}

	public boolean sendMessage(String msg, String rec){
		//some business logic here
		return service.sendMessage(msg, rec);
	}
}
```
MyApplication类中通过调用MessageService层来执行具体的任务。
MessageService.java
```
package com.journaldev.di.services;

public interface MessageService {

	boolean sendMessage(String msg, String receipient);
}
```
MessageService只是一个接口，定义两个实现类。
FacebookService.java
```
package com.journaldev.di.services;

import javax.inject.Singleton;

//import com.google.inject.Singleton;

@Singleton
public class FacebookService implements MessageService {

	public boolean sendMessage(String msg, String receipient) {
		//some complex code to send Facebook message
		System.out.println("Message sent to Facebook user "+receipient+" with message="+msg);
		return true;
	}

}
```
EmailService.java

```
package com.journaldev.di.services;

import javax.inject.Singleton;

//import com.google.inject.Singleton;

@Singleton
public class EmailService implements MessageService {

	public boolean sendMessage(String msg, String receipient) {
		//some fancy code to send email
		System.out.println("Email Message sent to "+receipient+" with message="+msg);
		return true;
	}

}
```
写一个测试类如下：
```
package com.journaldev.di.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.journaldev.di.consumer.MyApplication;
import com.journaldev.di.injector.AppInjector;

public class ClientApplication {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AppInjector());

		MyApplication app = injector.getInstance(MyApplication.class);
		app.sendMessage("Hi Pankaj", "pankaj@abc.com");
	}

}
```
首先创建一个注入器injector；然后通过注入器取得对象实例；最后执行发送信息的方法。
问题在于我们写了两个MessageService的实现，一个是Email,一个是FacebookService。调用哪个呢？如果是Spring，在xml中配置一个bean就可以了。
Guice需要用代码来指定，这就是AppInjector类的作用了。
AppInjector.java
```
package com.journaldev.di.injector;

import com.google.inject.AbstractModule;
import com.journaldev.di.services.EmailService;
import com.journaldev.di.services.MessageService;

public class AppInjector extends AbstractModule {

	@Override
	protected void configure() {
		//bind the service to implementation class
		bind(MessageService.class).to(EmailService.class);
	    //bind(MessageService.class).to(FacebookService.class);
	}

}
```

在AppInjector.config()方法中,注明把EmailService绑定到MessageService接口中。
这样就实现了依赖注入的功能，如果需要换成FacebookService实现，则只需修改AppInjector就可以了，这里的改动对MyApplication完全是透明的。