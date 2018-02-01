
```
$ mkdir django_learn && cd django_learn
$ django-admin.py startproject hello_django
$ cd hello_django
$ python manage.py startapp hello_app
$ python manage.py syncdb
```
如何启动开发环境呢? 

```
python manage.py runserver
 
# 当提示端口被占用的时候，可以用其它端口：
python manage.py runserver 8001
python manage.py runserver 9999
 
# 监听所有可用 ip
python manage.py runserver 0.0.0.0:8000
# 如果是外网或者局域网电脑上可以用其它电脑查看开发服务器
# 访问对应的 ip加端口，比如 http://172.16.20.2:8000
```

如何部署服务器?

```
如果部署在服务器呢？
uwsgi + nginx 配合
```
