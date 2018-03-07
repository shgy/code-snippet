1. 安装
Ubuntu
You can add our APT repository to your Ubuntu system so as to easily install our packages and receive updates in the future (via the apt-get update command). To add the repository, just run the following commands (only need to run once for each system):

    # import our GPG key:
    wget -qO - https://openresty.org/package/pubkey.gpg | sudo apt-key add -

    # for installing the add-apt-repository command
    # (you can remove this package and its dependencies later):
    sudo apt-get -y install software-properties-common

    # add the our official APT repository:
    sudo add-apt-repository -y "deb http://openresty.org/package/ubuntu $(lsb_release -sc) main"

    # to update the APT index:
    sudo apt-get update
Then you can install a package, say, openresty, like this:

    sudo apt-get install openresty
This package also recommends the openresty-opm and openresty-restydoc packages so the latter two will also automatically get installed by default. If that is not what you want, you can disable the automatic installation of recommended packages like this:

    sudo apt-get install --no-install-recommends openresty
See the OpenResty Deb Packages page for more details on all available packages in this repository.


2. 构建简单的项目
Prepare directory layout
We first create a separate directory for our experiments. You can use an arbitrary directory. Here for simplicity, we just use ~/work:

mkdir ~/work
cd ~/work
mkdir logs/ conf/
Note that we've also created the logs/ directory for logging files and conf/ for our config files.

Prepare the nginx.conf config file
Create a simple plain text file named conf/nginx.conf with the following contents in it:

worker_processes  1;
error_log logs/error.log;
events {
    worker_connections 1024;
}
http {
    server {
        listen 8080;
        location / {
            default_type text/html;
            content_by_lua '
                ngx.say("<p>hello, world</p>")
            ';
        }
    }
}
If you're familiar with Nginx configuration, it should look very familiar to you. OpenResty is just an enhanced version of Nginx by means of addon modules anyway. You can take advantage of all the existing goodies in the Nginx world.

Start the Nginx server
Assuming you have installed OpenResty into /usr/local/openresty (this is the default), we make our nginx executable of our OpenResty installation available in our PATH environment:

PATH=/usr/local/openresty/nginx/sbin:$PATH
export PATH
Then we start the nginx server with our config file this way:

nginx -p `pwd`/ -c conf/nginx.conf
Error messages will go to the stderr device or the default error log files logs/error.log in the current working directory.

Access our HelloWorld web service
We can use curl to access our new web service that says HelloWorld:

curl http://localhost:8080/
If everything is okay, we should get the output

<p>hello, world</p>
You can surely point your favorite web browser to the location http://localhost:8080/.


3. 性能测试

安装http_load
```
wget http://www.acme.com/software/http_load/http_load-09Mar2016.tar.gz
tar -xf http_load-09Mar2016.tar.gz
cd http_load-09Mar2016
make
sudo make install
```
使用http_load进行测试
```
$ http_load -p 30 -s 10 urls.txt
166810 fetches, 30 max parallel, 3.3362e+06 bytes, in 10.0003 seconds
20 mean bytes/connection
16680.6 fetches/sec, 333611 bytes/sec
msecs/connect: 0.192864 mean, 3.596 max, 0.027 min
msecs/first-response: 0.62939 mean, 4.401 max, 0.422 min
HTTP response codes:
  code 200 -- 166810
```


使用ab进行测试
``` 
$ ab -n10000 -c70 -k http://localhost:8080/
This is ApacheBench, Version 2.3 <$Revision: 1706008 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        openresty/1.13.6.1
Server Hostname:        localhost
Server Port:            8080

Document Path:          /
Document Length:        20 bytes

Concurrency Level:      70
Time taken for tests:   0.354 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    9930
Total transferred:      1729650 bytes
HTML transferred:       200000 bytes
Requests per second:    28228.81 [#/sec] (mean)
Time per request:       2.480 [ms] (mean)
Time per request:       0.035 [ms] (mean, across all concurrent requests)
Transfer rate:          4768.16 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.5      0       7
Processing:     0    2   1.8      2      50
Waiting:        0    2   1.8      2      50
Total:          0    2   1.9      2      51

Percentage of the requests served within a certain time (ms)
  50%      2
  66%      2
  75%      2
  80%      3
  90%      3
  95%      5
  98%      6
  99%      7
 100%     51 (longest request)
```

使用ab, qps能达到28K