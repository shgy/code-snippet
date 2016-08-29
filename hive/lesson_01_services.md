hive依赖于hadoop, 因此安装hive之前, 机器上必须安装了hadoop.hive的安装很简单, 两步就OK: 
补充:
Hive也可以不依赖于hadoop, 当然这样的配置只用于学习环境,即在/etc/profile文件中添加如下的命令
```
export HIVE_OPTS='--hiveconf mapred.job.tracker=local --hiveconf fs.default.name=file:///tmp \
     --hiveconf hive.metastore.warehouse.dir=file:///tmp/warehouse \
	 	     --hiveconf javax.jdo.option.ConnectionURL=jdbc:derby:;databaseName=/tmp/metastore_db;create=true'
```
即启用Hive的本地模式. 这样启动Hive之前就不用启动Hadoop了.方便Hive的学习.


1. 将Hive的tar.gz文件解压到/opt目录.
2. 在/etc/profile文件中配置HIVE_HOME即可.

安装好hive后, 执行hive --help
```
$ hive --help
Usage ./hive <parameters> --service serviceName <service parameters>
Service List: beeline cli help hiveburninclient hiveserver2 hiveserver hwi jar lineage metastore metatool orcfiledump rcfilecat schemaTool version 
Parameters parsed:
  --auxpath : Auxillary jars 
  --config : Hive configuration directory
  --service : Starts specific service/component. cli is default
Parameters used:
  HADOOP_HOME or HADOOP_PREFIX : Hadoop install directory
  HIVE_OPT : Hive options
For help on a particular service:
  ./hive --service serviceName --help
Debug help:  ./hive --debug --help
```
(注: auxillary 的中文意思是"辅助的")

从help中可以看到Hive核心的Service有:
beeline cli help hiveburninclient hiveserver2 hiveserver hwi jar lineage metastore metatool orcfiledump rcfilecat schemaTool version

其中, 像help version 属于见文知义, 不必赘述.

### hive服务启动的方式 ###
hive启动服务的方式为`hive <parameters> --service serviceName <service parameters>`:
例如: 
```
$ hive --service version
Hive 1.1.1
Subversion git://glacier.local/Users/chao/Documents/hive -r 3e8d832a1a8e2b12029adcb55862cf040098ef0f
Compiled by chao on Thu May 14 15:23:15 PDT 2015
From source with checksum 5820e7473159988fd33a0afcb10be30a
```
当然, 直接使用`hive --version`也是可以的, 只不过hive命令的shell脚本对其进行了转换而已. 可以通过查看hive命令的shell脚本代码验证这一点.
```
# cat -n  hive  
    30	while [ $# -gt 0 ]; do
    31	  case "$1" in
    32	    --version)
    33	      shift
    34	      SERVICE=version
    35	      ;;
    36	    --service)
    37	      shift
    38	      SERVICE=$1
    39	      shift
    40	      ;;
    41	    --rcfilecat)
    42	      SERVICE=rcfilecat
    43	      shift
    44	      ;;
    45	    --orcfiledump)
    46	      SERVICE=orcfiledump
    47	      shift
    48	      ;;
    49	    --help)
    50	      HELP=_help
    51	      shift
    52	      ;;
    53	    --debug*)
    54	      DEBUG=$1
    55	      shift
    56	      ;;
    57	    *)
    58	      break
    59	      ;;
    60	  esac
```
同理, $HIVE_HOME/bin目录下其它的几个命令,如`beeline`, `hiveserver2`, `metatool`, `schematool`, 都是这样的. 
```
$ cat beeline
...
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/hive --service beeline "$@"

$ cat metatool
...
hive --service metatool "$@"
```

如果直接使用hive命令, 什么都不带, 默认是启动cli服务.
```
$ cat -n hive
 63 if [ "$SERVICE" = "" ] ; then
 64   if [ "$HELP" = "_help" ] ; then
 65     SERVICE="help"
 66   else
 67     SERVICE="cli"
 68   fi
 69 fi

```
顺便说一句, 这几个服务单独拎出来, 可见其在hive中的重要程度. 

###hive各个服务的功能 ###
-----
| service | 说明 |
| ------------- |:-------------:| 
| cli              | 交互式命令行, 已经废弃, 由beeline取代 |
| beeline      |新一代的交互式命令行, 可以远程连接hive, 需要hiveserver2服务启动. |
| metatool   | 元数据操作相关.  |
| hiveserver2| 服务器: 基于thrift的hive服务, 通过它可以远程操作hive; |
| schematool| 元数据操作相关.  |

metatool的典型应用场景:
参考: https://cwiki.apache.org/confluence/display/Hive/Hive+MetaTool

hadoop的fs.defaultFS变更；由于hadoop的升级或者其它原因, fs.defaultFS变化了. 这时候hive就无法正常使用了, 因为hive建表的时候保存了数据的相关路径. 这时候就需要使用`metatool -updateLocation hdfs://oldpath hdfs:newpath` 来修改FSRoots. 然后使用`metatool -listFsRoot`查看是否修改成功.

schematool的典型应用场景:
参考: https://cwiki.apache.org/confluence/display/Hive/Hive+Schema+Tool 

默认情况下, hive是不会验证metastore schema与hive 执行jar包的版本是否匹配. 这在hive升级后,有可能出现不匹配的问题.因此, 可以使用命令查看两者是否一致, 或者升级metastore schema. 
```
schematool -dbType mysql -info
Metastore connection URL:	 jdbc:derby:;databaseName=metastore_db;create=true
Metastore Connection Driver :	 org.apache.derby.jdbc.EmbeddedDriver
Metastore connection User:	 APP
Hive distribution version:	 1.1.0
Metastore schema version:	 1.1.0
schemaTool completed
```


