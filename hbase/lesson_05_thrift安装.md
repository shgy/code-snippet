step 1:

```

sudo apt-get install libboost-dev libboost-test-dev libboost-program-options-dev libboost-system-dev libboost-filesystem-dev libevent-dev automake libtool flex bison pkg-config g++ libssl-dev thrift-compiler```

step 2:

```

wget http://ftp.acc.umu.se/mirror/cdimage/snapshot/Debian/pool/main/a/automake-1.14/automake_1.14.1-3_all.deb

sudo dpkg -i automake_1.14.1-3_all.deb

```

step 3:

```

wget http://archive.apache.org/dist/thrift/0.8.0/thrift-0.8.0.tar.gz

tar -zxvf thrift-0.8.0.tar.gz

```

step 4:

```

./configure --prefix=/opt/thrift-0.8.0

sudo make && sudo make install

```


