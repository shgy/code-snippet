想在spring-data-redis中使用redis的pipeline功能。 结果发现jedis不支持redis集群模式下的pipeline, 就找到了luttuce.
但是luttuce的文档有点简约， 就记录一下操作的代码， 备用。 毕竟redis基本上是高可用系统的标配了。

```
package com.lettuce.demo;

import com.google.common.collect.Lists;
import com.lambdaworks.redis.*;
import com.lambdaworks.redis.cluster.RedisAdvancedClusterAsyncConnection;
import com.lambdaworks.redis.cluster.RedisAdvancedClusterConnection;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.protocol.SetArgs;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @program: springdemo
 * @description:
 * @author: guangying.shuai
 * @create: 2018-05-17 10:32
 *
 *  Lettuce 对redis的基本操作
 */
public class Basic {

    private static void testNoPipeline(RedisClusterClient redisClusterClient){
        RedisAdvancedClusterConnection<String, String> conn = redisClusterClient.connectCluster();
        long start = System.currentTimeMillis();
        for(int k=0;k<10;k++){
            SetArgs args1 = SetArgs.Builder.ex(100);
            for(int i=1;i<1000;i++){
                conn.set("shgy_test"+(k*1000+i), "Hello, Redis1!"+(k*1000+i), args1);
            }

        }
        long end = System.currentTimeMillis();

        System.out.println("no pipeline spent: " + (end-start));
        conn.close();
    }

    private static void testGetValue(RedisClusterClient redisClusterClient){
        RedisAdvancedClusterConnection<String, Long> conn = redisClusterClient.connectCluster(new DefaultRedisCodec());

        String key = "1:shgy_test1";
        conn.set(key,1L);
        System.out.println(key+"="+conn.get(key));

        conn.close();
    }

 private static class DefaultRedisCodec extends RedisCodec<String,Long> {
        private GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        @Override
        public String decodeKey(ByteBuffer bytes) {
            byte[] b = new byte[bytes.remaining()];
            bytes.get(b);
            return serializer.deserialize(b, String.class);
        }

        @Override
        public Long decodeValue(ByteBuffer bytes) {
            byte[] b = new byte[bytes.remaining()];
            bytes.get(b);
            return serializer.deserialize(b, Long.class);

        }

        @Override
        public byte[] encodeKey(String key) {

            return  serializer.serialize(key);
        }

        @Override
        public byte[] encodeValue(Long value) {
            return serializer.serialize(value);
        }
    }
    private static void testPipeline(RedisClusterClient redisClusterClient) throws ExecutionException, InterruptedException {
        RedisAdvancedClusterAsyncConnection<String, Long> redisClusterConn = redisClusterClient.connectClusterAsync(new DefaultRedisCodec());
        redisClusterConn.setAutoFlushCommands(false);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        System.out.println("Connected to Redis");
        long start = System.currentTimeMillis();
        for(int k=0;k<10;k++){
            SetArgs args1 = SetArgs.Builder.ex(500);
            List<RedisFuture<String>> futureList = Lists.newArrayList();
            for(int i=1;i<1000;i++){

//                futureList.add(redisClusterConn.hset("shgy_test"+(k*1000+i),"imei1",System.currentTimeMillis()));
                futureList.add(redisClusterConn.set("shgy_test"+(k*1000+i),(k*1000L+i), args1));
            }
            redisClusterConn.flushCommands();
            boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS, futureList.toArray(new RedisFuture[futureList.size()]));
            System.out.println( k+ "是否成功: "+result);
        }
        long end = System.currentTimeMillis();

        System.out.println("pipeline spent: " + (end-start));
        redisClusterConn.setAutoFlushCommands(true);

        redisClusterConn.close();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Syntax: redis://[password@]host[:port]
//        RedisClient redisClient =  RedisClient.create("redis://password@localhost:6379/0");
//        RedisConnection<String, String> connection = redisClient.connect();
//
//        connection.set("key", "Hello, Redis!");
//
//        connection.close();
//        redisClient.shutdown();

        // =====================================================================================


//        RedisClusterClient redisClusterClient = RedisClusterClient.create("redis://@172.25.38.98:6379");
////        testMethodExecute(redisClusterClient);
////        testPipeline(redisClusterClient);
//        testGetValue(redisClusterClient);
//        redisClusterClient.shutdown();

         ThreadPoolExecutor executor = new ThreadPoolExecutor(5,10, 30000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(0),
                new ThreadPoolExecutor.AbortPolicy());
    }
}

```
