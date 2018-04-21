1 pom.xml

```

  <dependencies>

    <dependency>

		<groupId>org.elasticsearch</groupId>

		<artifactId>elasticsearch</artifactId>

		<version>1.6.0</version>

	</dependency>

	<dependency>

			<groupId>log4j</groupId>

			<artifactId>log4j</artifactId>

			<version>1.2.17</version>

		</dependency>

  </dependencies>

```

2 log4j.properties

```

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

log4j.appender.file.File=${catalina.home}/logs/myapp.log

#log4j.appender.file.File=logs/eshbase.log

log4j.appender.file.MaxFileSize=5MB

log4j.appender.file.MaxBackupIndex=10

log4j.appender.file.layout=org.apache.log4j.PatternLayout

log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

```



3 Java代码

```

public class BatchIndex {

	 private Client client;

	 private static Logger logger = Logger.getLogger(BatchIndex.class);

	 private Random ran;

	    public void init() {

	        client = new TransportClient()

	                .addTransportAddress(new InetSocketTransportAddress(

	                        "localhost", 9300));

	        ran = new Random(new Date().getTime());

	    }

	 

	    public void close() {

	        client.close();

	    }

	    

	   private String randStr(){

		   int max = 100,min = 50;

		   int s = ran.nextInt(max) % (max - min + 1) + min;

	        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  

	        StringBuffer sb = new StringBuffer();  

	          

	        for(int i = 0 ; i < s; ++i){  

	            int number = ran.nextInt(62);//[0,62)  

	              

	            sb.append(str.charAt(number));  

	        }  

	        return sb.toString();  

	   }

	    

	   private Map<String,String> mk_source(int docId){

		   

		   Map<String,String> map  = new HashMap<String,String>(200);

		   for(int i=0;i<200;i++){

			   map.put("message "+i, randStr());

		   }

		   return map;

		   

	   }

	    

	    public void batchIndex(int start, int count){

	    	BulkRequestBuilder builder = new BulkRequestBuilder(client);

	    	IndexRequest ireq ; 

	    	for(int i = start;i<start+count;i++){

	    		ireq = new IndexRequest("qyxgxx_2015_09_24", "tweet");

	    		ireq.id(Integer.toString(i)).source(mk_source(i));

	    		builder.add(ireq);

	    		

	    		

	    	}

	    	client.bulk(builder.request()).actionGet();

	    }

	    

	    public static void main(String[] args) {

			BatchIndex bi = new BatchIndex();

			bi.init();

			for(int i=0;i<1000000;i+=100){

				logger.info("start handle " + i);

				bi.batchIndex(i, 100);

				

			}

			bi.close();

		}

	   

}

```
