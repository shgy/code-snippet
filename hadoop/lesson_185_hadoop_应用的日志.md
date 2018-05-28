日志对于定位系统的问题非常关键:

0. 对hadoop进行配置：
```
$ cat config/mapred-site.xml 
<?xml version="1.0"?>
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
	<name>mapreduce.jobhistory.address</name>
	<value>hadoop-master:10020</value>
    </property>
    <property>
	<name>mapreduce.jobhistory.webapp.address</name>
	<value>hadoop-master:19888</value>
    </property>
</configuration>
shgy@shgy-desktop:~/docker-hadoop-cluster$ cat config/yarn-site.xml 
<?xml version="1.0"?>
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>hadoop-master</value>
    </property>
<property>    
    <name>yarn.log-aggregation-enable</name>    
    <value>true</value>    
</property>     
<property>
    <name>yarn.log.server.url</name>
    <value>http://hadoop-master:19888/jobhistory/logs</value>
</property>

</configuration>

```


1. 自己编写wordcount， 添加日志
```
    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            LOG.info("info in reduce, start word count:"+key);
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
```

2. 打包
```
maven-assembly-plugin
```
3. 上传到docker中
```
sudo docker cp hadoop_workspace/wordcount/target/wordcount-1.0-jar-with-dependencies.jar hadoop-master:/root/
```
4. 提交:
```
hadoop jar wordcount-1.0-jar-with-dependencies.jar input output
```

5. 最后通过浏览器看到hadoop的日志
http://hadoop-master:8088/cluster 到ApplicationMaster, 然后选择reduce任务， 选择logs就能看到日志了。

具体的代码参考`lesson_185_code`

```
2018-05-28 15:37:47,682 INFO [main] com.shgy.WordCount: info in reduce, start word count:Docker
2018-05-28 15:37:47,683 INFO [main] com.shgy.WordCount: info in reduce, start word count:Hadoop
2018-05-28 15:37:47,683 INFO [main] com.shgy.WordCount: info in reduce, start word count:Hello
```


参考:
https://stackoverflow.com/questions/24076192/yarn-jobhistory-error-failed-redirect-for-container-1400260444475-3309-01-00000?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
