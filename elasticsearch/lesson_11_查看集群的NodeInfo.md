通常, 管理ES的集群, 我们需要查看ES的集群状态. 假如我们知道ES的rest端口(默认是9200), 那么很简单,
一个rest命令就OK了`curl -XGET 'http://localhost:9200/_nodes'`. 如果是在代码中实现相同的功能呢? 
1. 使用HttpClient 调用相关api.
2. 使用ES提供的JavaAPI. 
当我们手里只有ES的tcp连接信息, 可以通过JavaAPI反查看集群的其他信息. 

相关的Java代码:
```
package com.sgh;

import org.apache.commons.cli.*;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodesInfoDemo {

    private static CommandLine init_opts(String[] args) {

        // create Options object
        Options options = new Options();
        // add t option
        options.addOption("n", "es.name",true, "elasticsearch name");
        options.addOption("h", "es.host",true, "elasticsearch host");

        CommandLineParser parser = new DefaultParser();

        try{
            CommandLine cmd = parser.parse( options, args);
            // 如果包含有-h或--help，则打印出帮助信息
            if (!cmd.hasOption("h") && !cmd.hasOption("n")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("Main", "", options, "");
                System.exit(1);
            }
            return  cmd;
        }catch (Exception e){
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Main", "", options, "");
            System.exit(1);

        }
        return null;
    }

    private static void nodeinfos(String es_name, String es_host) throws UnknownHostException {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", es_name).build();
        TransportClient client = TransportClient.builder().settings(settings).build();

        try{
            for(String host: es_host.split(",")){
                String[] host_port = host.split(":");
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host_port[0]), Integer.parseInt(host_port[1])));
            }

            NodesInfoRequest req = new NodesInfoRequest();
            NodesInfoResponse resp = client.admin().cluster().nodesInfo(req).actionGet();
            System.out.println(resp);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            client.close();
        }

    }

    public static void main(String[] args) throws UnknownHostException {

        CommandLine cmd = init_opts(args);
        String es_name = cmd.getOptionValue("es.name");
        String es_host = cmd.getOptionValue("es.host");
        nodeinfos(es_name, es_host);
    }
}

```
maven的pom.xml配置:
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.sgh</groupId>
  <artifactId>esdemo</artifactId>
  <version>1.0-SNAPSHOT</version>


  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
    <es.version>2.1.1</es.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>${es.version}</version>
    </dependency>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>jna</artifactId>
      <version>4.4.0</version>
    </dependency>
    <dependency>
      <groupId>commons-cli</groupId>
      <artifactId>commons-cli</artifactId>
      <version>1.4</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

  </dependencies>

  <build>
      <plugins>
        <plugin>
          <artifactId>maven-assembly-plugin</artifactId>
          <version>3.1.0</version>
          <configuration>
            <archive>
              <manifest>
                <mainClass>com.sgh.NodesInfoDemo</mainClass>
              </manifest>
            </archive>
            <descriptorRefs>
              <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
          </configuration>
          <executions>
            <execution>
              <id>make-assembly</id> <!-- this is used for inheritance merges -->
              <phase>package</phase> <!-- bind to the packaging phase -->
              <goals>
                <goal>single</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
  </build>
</project>
```

这里需要注意层次结构是plugins/plugin,而不是plugins/pluginmanagement/plugin. 这可是花了几个小时找到的答案.
这样执行mvn package时, assembly 插件才会执行.
启动es后, 使用如下的命令就可以看到集群中node的所有信息:
```
$ java -jar esdemo-1.0-SNAPSHOT-jar-with-dependencies.jar -hlocalhost:9300 -nelasticsearch
Apr 29, 2018 9:55:46 PM org.elasticsearch.plugins.PluginsService <init>
INFO: [Shadow-Hunter] loaded [], sites []
{
  "cluster_name" : "elasticsearch",
  "nodes" : {
    "38LbL_IwTrKlzkTtQADQ0w" : {
      "name" : "Morris Bench",
      "transport_address" : "127.0.0.1:9300",
      "host" : "127.0.0.1",
      "ip" : "127.0.0.1",
      "version" : "2.1.1",
      "build" : "40e2c53",
      "http_address" : "127.0.0.1:9200",
      "settings" : {
        "path" : {
          "logs" : "/opt/elasticsearch-2.1.1/logs",
          "home" : "/opt/elasticsearch-2.1.1"
        },
        "cluster" : {
          "name" : "elasticsearch"
        },
        "config" : {
          "ignore_system_properties" : "true"
        },
        "client" : {
          "type" : "node"
        },
        "name" : "Morris Bench"
      },
      "os" : {
        "refresh_interval_in_millis" : 1000,
        "available_processors" : 4,
        "allocated_processors" : 4
      },
      "process" : {
        "refresh_interval_in_millis" : 1000,
        "id" : 10128,
        "mlockall" : false
      },
      "jvm" : {
        "pid" : 10128,
        "version" : "1.7.0_80",
        "vm_name" : "Java HotSpot(TM) 64-Bit Server VM",
        "vm_version" : "24.80-b11",
        "vm_vendor" : "Oracle Corporation",
        "start_time_in_millis" : 1524997150060,
        "mem" : {
          "heap_init_in_bytes" : 268435456,
          "heap_max_in_bytes" : 1038876672,
          "non_heap_init_in_bytes" : 24313856,
          "non_heap_max_in_bytes" : 136314880,
          "direct_max_in_bytes" : 1038876672
        },
        "gc_collectors" : [ "ParNew", "ConcurrentMarkSweep" ],
        "memory_pools" : [ "Code Cache", "Par Eden Space", "Par Survivor Space", "CMS Old Gen", "CMS Perm Gen" ]
      },
      "thread_pool" : {
        "generic" : {
          "type" : "cached",
          "keep_alive" : "30s",
          "queue_size" : -1
        },
        "index" : {
          "type" : "fixed",
          "min" : 4,
          "max" : 4,
          "queue_size" : 200
        },
        "fetch_shard_store" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 8,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "get" : {
          "type" : "fixed",
          "min" : 4,
          "max" : 4,
          "queue_size" : 1000
        },
        "snapshot" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 2,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "force_merge" : {
          "type" : "fixed",
          "min" : 1,
          "max" : 1,
          "queue_size" : -1
        },
        "suggest" : {
          "type" : "fixed",
          "min" : 4,
          "max" : 4,
          "queue_size" : 1000
        },
        "bulk" : {
          "type" : "fixed",
          "min" : 4,
          "max" : 4,
          "queue_size" : 50
        },
        "warmer" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 2,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "flush" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 2,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "search" : {
          "type" : "fixed",
          "min" : 7,
          "max" : 7,
          "queue_size" : 1000
        },
        "fetch_shard_started" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 8,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "listener" : {
          "type" : "fixed",
          "min" : 2,
          "max" : 2,
          "queue_size" : -1
        },
        "percolate" : {
          "type" : "fixed",
          "min" : 4,
          "max" : 4,
          "queue_size" : 1000
        },
        "refresh" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 2,
          "keep_alive" : "5m",
          "queue_size" : -1
        },
        "management" : {
          "type" : "scaling",
          "min" : 1,
          "max" : 5,
          "keep_alive" : "5m",
          "queue_size" : -1
        }
      },
      "transport" : {
        "bound_address" : [ "127.0.0.1:9300", "[::1]:9300" ],
        "publish_address" : "127.0.0.1:9300",
        "profiles" : { }
      },
      "http" : {
        "bound_address" : [ "127.0.0.1:9200", "[::1]:9200" ],
        "publish_address" : "127.0.0.1:9200",
        "max_content_length_in_bytes" : 104857600
      },
      "plugins" : [ {
        "name" : "head",
        "version" : "master",
        "description" : "head - A web front end for an elastic search cluster",
        "url" : "/_plugin/head/",
        "jvm" : false,
        "site" : true
      }, {
        "name" : "kopf",
        "version" : "2.1.2",
        "description" : "kopf - simple web administration tool for Elasticsearch",
        "url" : "/_plugin/kopf/",
        "jvm" : false,
        "site" : true
      }, {
        "name" : "mapper-murmur3",
        "version" : "2.1.1",
        "description" : "The Mapper Murmur3 plugin allows to compute hashes of a field's values at index-time and to store them in the index.",
        "jvm" : true,
        "classname" : "org.elasticsearch.plugin.mapper.MapperMurmur3Plugin",
        "isolated" : true,
        "site" : false
      } ]
    }
  }
}

```
 

参考:
https://www.elastic.co/guide/en/elasticsearch/reference/2.1/cluster-nodes-info.html 
