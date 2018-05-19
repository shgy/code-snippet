使用Lettuce， 默认只能处理String, String的情况。 但是， 我们业务中更多的是String, Int等其他的场景。
怎么办？

```
package com.springapp.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.codec.RedisCodec;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.nio.ByteBuffer;

public class Basic {
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

    public static void main(String[] args) {

        RedisClient redisClient =  RedisClient.create("redis://@localhost:6379");
        RedisConnection<String, Long> connection = redisClient.connect(new DefaultRedisCodec()); //
        connection.set("key",5L);
        Long val = connection.get("key");
        System.out.println(val);
        connection.close();
        redisClient.shutdown();


    }
}

```
就是自定义codec， 这个实现参考`ByteArrayCodec`。
