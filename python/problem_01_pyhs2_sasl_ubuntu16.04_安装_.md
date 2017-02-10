在ubuntu-16.04上安装hive的thrift客户端pyhs2, 其中用到了sasl, 安装没有问题, 但是在使用的过程中,报如下的错误:
```
sasl/saslwrapper.so: undefined symbol:
```
很明显, 这个库是python调用c/c++实现的. 于是找到了sasl的github: `https://github.com/cloudera/python-sasl`
clone下来后,使用`python setup.py build` 发现了两段编译的命令
```
building 'sasl.saslwrapper' extension
gcc -pthread -fno-strict-aliasing -g -O2 -DNDEBUG -g -fwrapv -O3 -Wall -Wstrict-prototypes -fPIC -Isasl -I/usr/local/anaconda/include/python2.7 -c sasl/saslwrapper.cpp -o build/temp.linux-x86_64-2.7/sasl/saslwrapper.o
cc1plus: warning: command line option ‘-Wstrict-prototypes’ is valid for C/ObjC but not for C++ [enabled by default]
In file included from sasl/saslwrapper.cpp:254:0:
sasl/saslwrapper.h: In member function ‘void saslwrapper::ClientImpl::interact(sasl_interact_t*)’:
sasl/saslwrapper.h:437:11: warning: unused variable ‘input’ [-Wunused-variable]
     char* input;
           ^
g++ -pthread -shared -L/usr/local/anaconda/lib -Wl,-rpath=/usr/local/anaconda/lib,--no-as-needed build/temp.linux-x86_64-2.7/sasl/saslwrapper.o -L/usr/local/anaconda/lib -lsasl2 -lpython2.7 -o build/lib.linux-x86_64-2.7/sasl/saslwrapper.so
```
这个库在ubuntu-14.04上面使用没有问题, 但是在ubuntu-16.04上出问题了. 很自然就想到了对比gcc的版本
果然, ubuntu-16.04上是gcc-5.4, 最新GCC.  google查询到
```
Ubuntu16.04 集成了GCC 5.4.0， 但是很多软件不支持这么高版本的GCC。
```
降级:
```
sudo apt-get install g++-4.8
ln -s /usr/bin/g++-4.8 /usr/bin/g++
```
然后编译, 通过, 正常使用.

参考:
http://blog.csdn.net/striker_v/article/details/51920627
