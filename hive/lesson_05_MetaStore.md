Hive中创建的表, 表分区都存储在第三方的数据库中, 默认是一个内嵌的derby数据库. 在生产环境中可以配置成mysql/oracle等关系型数据库.
Hive使用JPOX作为ORM方案.
由于Hive的元数据是无状态的, 因此第三方数据库,可以配置成高可用的方式,提供给Hive使用.
创建好数据库,并在hive-site.xml中配置好数据库的地址后, Hive即会自动建表, 也可以配置成不自动建表.
如果使用Hive的本地模式, 则如下配置即可:
```
 export HIVE_OPTS='--hiveconf mapred.job.tracker=local --hiveconf fs.default.name=file:///tmp
       --hiveconf hive.metastore.warehouse.dir=file:///opt/hive-1.1.0/warehouse
	   --hiveconf javax.jdo.option.ConnectionURL=jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true
	   --hiveconf javax.jdo.option.ConnectionDriverName=com.mysql.jdbc.Driver
	   --hiveconf javax.jdo.option.ConnectionUserName=root
	   --hiveconf javax.jdo.option.ConnectionPassword=passwd'
```
 注意需要将MySQL的JDBC添加到Hive的lib中去.

 MySQL编码设置. 一般习惯于将MySQL数据库的编码设置为UTF-8, 但是这样的话, 创建表的时候会出现如下的异常:
```
 com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: Specified key was too long; max key length is 767 bytes
```
解决办法为:
```
 alter database hive character set latin1;
 ```




