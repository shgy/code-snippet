在Hbase中，实现了两种方式的coprocessor，一种是observers；一种是endpoint。如果用数据库类比，那么observer有点像触发器；endpoint像存储过程。

关于原理及说明，可以参考hbase的文档，描述已经非常详细。动手实现过程记录如下：

1 创建maven项目，引入hbase-server包；配置log4j日志。

```xml

	<dependency>

		<groupId>org.apache.hbase</groupId>

		<artifactId>hbase-server</artifactId>

		<version>1.0.0</version>

	</dependency>

```

```

log4j.properties

# Root logger option

log4j.rootLogger=INFO, stdout, file



# Redirect log messages to console

log4j.appender.stdout=org.apache.log4j.ConsoleAppender

log4j.appender.stdout.Target=System.out

log4j.appender.stdout.layout=org.apache.log4j.PatternLayout

log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n



# Redirect log messages to a log file

log4j.appender.file=org.apache.log4j.RollingFileAppender

#outputs to Tomcat home

log4j.appender.file.File=esprocessor.log

#log4j.appender.file.File=logs/eshbase.log

log4j.appender.file.MaxFileSize=5MB

log4j.appender.file.MaxBackupIndex=10

log4j.appender.file.layout=org.apache.log4j.PatternLayout

log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

```

2 创建Java类，如下：

```

package essync;



import java.io.IOException;



import org.apache.hadoop.hbase.client.Delete;

import org.apache.hadoop.hbase.client.Durability;

import org.apache.hadoop.hbase.client.Put;

import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;

import org.apache.hadoop.hbase.coprocessor.ObserverContext;

import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

import org.apache.hadoop.hbase.regionserver.wal.WALEdit;

import org.apache.log4j.Logger;



public class ESSyncRegionObserver extends BaseRegionObserver{

	private static final Logger logger = Logger.getLogger(ESSyncRegionObserver.class);

	@Override

	public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability)

			throws IOException {

		// TODO Auto-generated method stub

		super.postPut(e, put, edit, durability);

		logger.info("postPut!");

	}



	@Override

	public void postDelete(ObserverContext<RegionCoprocessorEnvironment> e, Delete delete, WALEdit edit,

			Durability durability) throws IOException {

		// TODO Auto-generated method stub

		super.postDelete(e, delete, edit, durability);

		logger.info("postDelete!");

	}



}

```

第三步，打包[export]->[Java/jar file] ，命名为essync.jar ,然后将jar文件上传到hdfs。这里需要注意，上传的目录为/user/hadoop/；<登录用户为hadoop>

第四步：hbase shell

```

 create 'testes1', 'cf'

 disable 'testes1'

 alter 'testes1', METHOD => 'table_att', 'coprocessor'=>'hdfs://host:port/user/hadoop/essync.jar|essync.ESSyncRegionObserver|1073741823'

 enable 'testes1'

 put 'testes1', 'row1', 'cf:a','value1'

```

第五步：查看日志

到登录到CDH Manager,然后选择[Diagnositics]->[logs], 输入‘postput’, 然后搜索，即可看到输出的日志。
