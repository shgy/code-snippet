redis一般需要通过源码安装. 这个步骤在redis源码的README.md文档中有说明, 按说明来就是了, 很简单.
```
make
make test
make PREFIX=/opt/redis-4.0.8 install
cp ../redis.conf /opt/redis-4.0.8
```
安装好后,直接启动server
```
/opt/redis-4.0.8/bin/redis-server
```

这时候,就可以启动client访问redis了, 不需要密码
```
/opt/redis-4.0.8/bin/redis-cli
127.0.0.1:6379> keys *
(empty list or set)
127.0.0.1:6379> set "key1" "hello"
OK
127.0.0.1:6379> get "key1"
"hello"
127.0.0.1:6379> keys "*"
1) "key1"

```
剩下的就是使用redis的其他命令了.
