这涉及到ES/Kafka/Spark, 为了简单起见.分为三步走.

1. 从Kafka中读出数据

2. 将简单的数据写入到Kafka

3. 对接两端, 从Kafka中读数据；然后写入到ES



elasticsearch-hadoop支持将数据分发到多个es中的多个type



maven pom.xml文件

```

<dependencies>

  		<!-- Spark -->

		<dependency>

			<groupId>org.apache.spark</groupId>

			<artifactId>spark-core_2.10</artifactId>

			<version>1.3.0</version>

			<scope>provided</scope>

		</dependency>



		<!-- Spark Steaming -->

		<dependency>

			<groupId>org.apache.spark</groupId>

			<artifactId>spark-streaming_2.10</artifactId>

			<version>1.3.0</version>

		</dependency>

		<dependency>

			<groupId>org.apache.spark</groupId>

			<artifactId>spark-streaming-kafka_2.10</artifactId>

			<version>1.3.0</version>

		</dependency>



		<!-- KAFKA -->

		<dependency>

			<groupId>org.apache.kafka</groupId>

			<artifactId>kafka_2.10</artifactId>

			<version>0.9.0.1</version>

		</dependency>

		

		<!-- ES -->

		<dependency>

			<groupId>org.elasticsearch</groupId>

			<artifactId>elasticsearch</artifactId>

			<version>1.7.0</version>

		</dependency>

		

		<dependency>

			<groupId>org.elasticsearch</groupId>

			<artifactId>elasticsearch-hadoop</artifactId>

			<version>2.2.0-m1</version>

		</dependency>

		

  </dependencies>

```

Java类

```

public class KafkaToEs {

	

	//日志对象

	private static final Logger LOGGER = Logger.getLogger(KafkaToEs.class);

//	public KafkaToEs

	private static final String INTERVAL = PropertiesUtil.getInstance().get("INTERVAL");

	//队列配置

	private static final String zkQuorum = PropertiesUtil.getInstance().get("KAFKA_zkQuorum");

	private static final String group = PropertiesUtil.getInstance().get("KAFKA_GROUP");

	private static final String topicss = PropertiesUtil.getInstance().get("KAFKA_QUEUE");

	private static final String threads = PropertiesUtil.getInstance().get("KAFKA_THREAD");

	

	//备份目录

	private static final String CHECK_DIR = PropertiesUtil.getInstance().get("KAFKA_CHECK_DIR");

	private static final String checkpoint = CHECK_DIR + topicss.hashCode() + "-" + group;

	

	//es配置

	private static final String ES_HOST = PropertiesUtil.getInstance().get("db.es.host");

	private static final int ES_PORT = Integer.parseInt(PropertiesUtil.getInstance().get("db.es.port"));

	private static final String ES_CLUSTER = PropertiesUtil.getInstance().get("db.es.cluster");

	private static final String ES_INDEX = PropertiesUtil.getInstance().get("db.es.index");

	private static final String ES_TABLE_PREFIX = PropertiesUtil.getInstance().get("db.es.table.prefix");

	

	private KafkaToEs() {}

	

	public static JavaStreamingContext createContext(boolean debug){

	// 获取SparkConf

		SparkConf sparkConf = new SparkConf().setAppName("dp-handler-pending-store");

		sparkConf.set("es.mapping.id", "_id");

		//设置调试模式

		if(debug){

			sparkConf.setMaster("local[*]");

		}

		// 定义每10秒处理一次

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(Integer.parseInt(INTERVAL) * 1000));

		//设置数据备份，防止丢数据

		jssc.checkpoint(checkpoint);

		// 定义线程数

		int numThreads = Integer.parseInt(threads);

		// 定义需要订阅的话题

		Map<String, Integer> topicMap = new HashMap<String, Integer>();

		String[] topics = topicss.split(",");

		for (String topic : topics) {

			topicMap.put(topic, numThreads);

		}

		HashMap<String, String> kafkaParams = new HashMap<String, String>();

	    kafkaParams.put("group.id", group);

	    kafkaParams.put("zookeeper.connect", zkQuorum);

	    kafkaParams.put("auto.offset.reset", "smallest");

		// 创建流

	    JavaPairReceiverInputDStream<String, String> messages = KafkaUtils.createStream(

	    	    	jssc,

	    	        String.class,

	    	        String.class,

	    	        StringDecoder.class,

	    	        StringDecoder.class, 

	    	        kafkaParams, topicMap, 

	    	        StorageLevel.MEMORY_AND_DISK_SER());

		// 转换数据流

		JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {

			@Override

			public String call(Tuple2<String, String> tuple2) {

				return tuple2._2();

			}

		});

		

		

		lines.filter(new Function<String, Boolean>() {

			

			@Override

			public Boolean call(String res) throws Exception {

				Map map = null;

				try {

					map = JacksonUtils.JsonToEntity(res, Map.class);

				} catch (Exception e) {

					LOGGER.error("Exception of JSON string to Map "+res, e);

					return false;

				}

				String _id = (String) map.get("_id");

				

				if(_id==null){LOGGER.error("_id not exist in data "+res);return false;}

				

				String table = (String)map.get("BBD_TABLE");

				

				if(table==null){LOGGER.error("table not exist in data "+res);return false;}

				return true;

			}

		}).map(new Function<String, Map<String,?>>() {

			@Override

			public Map<String,?> call(String res) throws Exception {

				// TODO Auto-generated method stub

				return JacksonUtils.JsonToEntity(res, Map.class);

			}

		}).foreachRDD(new Function<JavaRDD<Map<String,?>>, Void>() {

			@Override

			public Void call(JavaRDD<Map<String, ?>> v1) throws Exception {

				JavaEsSpark.saveToEs(v1, String.format("spark/%s_{BBD_TABLE}", ES_TABLE_PREFIX));

				return null;  

			}

		});

		

		//返回流对象

		return jssc;

	}

	

	/**

	 * 启动进程

	 */

	public static void process(final boolean debug){

		//创建工厂

		JavaStreamingContextFactory factory = new JavaStreamingContextFactory() {

			@Override

			public JavaStreamingContext create() {

				return createContext(debug);

			}

	    };

	    //防止丢失数据

	    JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(checkpoint, factory);

		//启动程序

		jssc.start();

		jssc.awaitTermination();

	}

	

	//程序入口

	public static void main(String[] args) throws IOException {

		args = new String[1];

		args[0]="debug";

		//配置是否为测试模式

		boolean debug = false;

		if(args.length == 1 && ("debug".equals(args[0]))){

			debug = true;

		}

		//启动程序

		process(debug);

	}

}

```
