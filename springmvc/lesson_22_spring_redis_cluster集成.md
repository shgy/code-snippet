这里只记录spring集成的相关方法，不记录redis集群的搭建过程。

1. redis.properties
```
spring.redis.cluster.nodes=10.48.193.201:7379,10.48.193.202:7380,10.48.193.203:9381
spring.redis.cluster.timeout=2000
spring.redis.cluster.max-redirects=8
```

2. applicationContext.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:cache="http://www.springframework.org/schema/cache" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
	http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache.xsd
	http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd">
	<context:property-placeholder location="classpath:redis.properties" />

	<bean class="com.redis.cluster.support.config.MonitorConfig" />
	<!-- 对静态资源文件的访问 -->
	<mvc:default-servlet-handler/>
	<mvc:annotation-driven />
	<context:component-scan base-package="com.redis.cluster.monitor" />
</beans>
```

3. MonitorConfig的Java代码
```
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.redis.cluster.support.serializer.DefaultKeySerializer;

@Configuration
public class MonitorConfig {
	@Value("${spring.redis.cluster.nodes}")
	private String clusterNodes;
	@Value("${spring.redis.cluster.timeout}")
	private Long timeout;
	@Value("${spring.redis.cluster.max-redirects}")
	private int redirects;
	@Value("${app-code:}")
	private String appCode;
	@Value("${expiration:0}")
	private long expiration;
	
	@Bean
	public RedisClusterConfiguration getClusterConfiguration(){
		Map<String, Object> source = new HashMap<String, Object>();
		source.put("spring.redis.cluster.nodes", clusterNodes);
		source.put("spring.redis.cluster.timeout", timeout);
		source.put("spring.redis.cluster.max-redirects", redirects);
		return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
	}
	
	@Bean
	public JedisConnectionFactory getConnectionFactory() {
		return new JedisConnectionFactory(getClusterConfiguration());
	}
	
	@Bean
	public JedisClusterConnection getJedisClusterConnection(){
		return (JedisClusterConnection) getConnectionFactory().getConnection();
	}
	
	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public RedisTemplate getRedisTemplate() {
		RedisTemplate clusterTemplate = new RedisTemplate();
		clusterTemplate.setConnectionFactory(getConnectionFactory());
		clusterTemplate.setKeySerializer(new DefaultKeySerializer());
		clusterTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return clusterTemplate;
	}
}

```

4. 集群的操作方式
```
@Service
public class ClusterServiceImpl implements ClusterService {
	private static final Log logger = LogFactory.getLog(ClusterServiceImpl.class);
	static {
		System.setProperty("line.separator", "\n");
	}
	@Autowired JedisClusterConnection clusterConnection;
	
	@Override
	public void info() {
		ClusterInfo info = clusterConnection.clusterGetClusterInfo();
		logger.info(info);
		RuntimeContainer.setRetMessage(info);
	}
}
```

其他的部分就可以依据业务自行扩展了。

参考：
https://github.com/zhengfc/redis-cluster-monitor

这个教程相当清楚， 基础， 实用。

===========================================================

这里虽然配置好了， 但是使用了Java代码。 其实是可以将一些代码的配置写入到xml中。

还有就是spring data redis不支持pipeline的方式操作集群。
