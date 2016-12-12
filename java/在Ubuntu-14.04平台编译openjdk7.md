Step 1: 下载http://download.java.net/openjdk/jdk7u40/promoted/b43/openjdk-7u40-fcs-src-b43-26_aug_2013.zip

Step 2: 编写测试脚本
```
:~/openjdk7/openjdk$ cat env_set.sh
export LANG=C
#Bootstrap JDK的安装路径
export ALT_BOOTDIR=/opt/jdk1.7.0_80
#不添加的话汇报一个路径错误  还是加上吧
export ALT_JDK_IMPORT_PATH=/opt/jdk1.7.0_80

#允许自动下载
export ALLOW_DOWNLOADS=true
#并行编译的线程数，设置为和CPU的内核数量一致即可
export HOTSPOT_BUILD_JOBS=4
export ALT_PARALLEL_COMPILE_JOBS=4

#比较本次build出来的映像与先前版本的差异,这对我们来说没有意义
#必须设置为false，否则sanity检查会报缺少先前版本的JDK的映像的报错提示
#如果自己已经设置Dev或者DEV_ONLY=true
export SKIP_COMPARE_IMAGES=true
 
#使用预编译头文件，不加这个编译会更慢一些
export USE_PRECOMPILED_HEADER=true

#要编译的内容
export BUILD_LANGTOOLS=true
export BUILD_JAXP=false
export BUILD_CORBA=false
export BUILD_JAXWS=false
export BUILD_HOTSPOT=true
export BUILD_JDK=true

#要编译的版本
#export SKIP_DEBUG_BUILD=false
#export SKIP_FASTDEBUG_BUILD=false
#export DEBUG_NAME=false

#把它设置为false可以避开javaws和浏览器Java插件之类的部分的build
BUILD_DEPLOY=false

#把它设置为false 就不会build出来安装包。因为安装包这里有些奇怪的依赖
#即便不build出它也已经能得到完整的JDK镜像，所以还是别build出来它好
BUILD_INSTALL=false

#编译结果所存放的路径
export ALT_OUTPUTDIR=/home/shgy/openjdk7/build

unset JAVA_HOME
unset CLASSPATH

make sanity && make 2>&1 | tee $ALT_OUTPUTDIR/build.log

```

第三步: 安装相关lib
```
sudo apt-get install libasound2-dev
sudo apt-get install libcups2-dev
```

第四步: 修改文件：hotspot/make/linux/Makefile, 去掉文件中所有的test_gamma

```
遇到错误Error:./gamma: relocation error: /usr/lib/jvm/java-7-openjdk-amd64/jre/lib/amd64/libjava.so: symbol JVM_FindClassFromCaller, version SUNWprivate_1.1 not defined in file libjvm.so with link time reference   
修改文件：hotspot/make/linux/Makefile   
去掉文件中所有的test_gamma即可
```

第五步: 编译, 成功

参考: 
http://m.w2bc.com/article/79497
http://cduym.iteye.com/blog/1892416
http://www.cnphp6.com/archives/122601



