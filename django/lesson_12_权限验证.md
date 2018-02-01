Django内部集成了一套提供了开箱即用的权限框架. 这也是很多人用Django的原因.





权限控制是一个系统的根基, 其根本目的在于资源的保护.它有两大核心:authentication(身份验证)和authorization(访问授权) .

一个相对安全好用的系统,需要考虑以下几个方面:

1 用户的密码强度控制(防止暴力破解)

2 用户的密码存储Hash设计(不要存储明文)

3 用户登录试错的次数控制(防止暴力破解)

4 灵活的访问权限控制(目前都是基于角色的访问控制)

5 基于接口编程或者易易于扩展的后端(pluggable backend)



使用如下的命令创建Django项目并启动后

```

django-admin startproject mywebsite && cd mywebsite

python manage.py makemigrations

python manage.py migrate

python manage.py createsuperuser --username shgy --email shgy@example.com

Password: 

Password (again): 

python manage.py runserver 0.0.0.0:8000

```

在浏览器中输入`http://localhost:8000/admin/login/`  即可看到登录界面



在这背后, Django做了哪些工作呢?

Django中内置了一些标准模块,都放在django.contrib包中:

```

/usr/local/lib/python2.7/dist-packages/django$ tree -L 1 -d contrib

contrib

├── admin  

├── admindocs

├── auth

├── contenttypes

├── flatpages

├── gis

├── humanize

├── messages

├── postgres

├── redirects

├── sessions

├── sitemaps

├── sites

├── staticfiles

├── syndication

└── webdesign

```

首先,看mywebsite/mywebsite/urls.py

```

urlpatterns = [

    url(r'^admin/', include(admin.site.urls)),

]

```

跳转到admin.site.urls

```

    @property

    def urls(self):

        return self.get_urls(), 'admin', self.name

```

然后找到:

```

urlpatterns = [

       url(r'^$', wrap(self.index), name='index'),

       url(r'^login/$', self.login, name='login'),

       url(r'^logout/$', wrap(self.logout), name='logout'),

       url(r'^password_change/$', wrap(self.password_change, cacheable=True), name='password_change'),

       url(r'^password_change/done/$', wrap(self.password_change_done, cacheable=True),

                name='password_change_done'),

       url(r'^jsi18n/$', wrap(self.i18n_javascript, cacheable=True), name='jsi18n'),

       url(r'^r/(?P<content_type_id>\d+)/(?P<object_id>.+)/$', wrap(contenttype_views.shortcut),

                name='view_on_site'),

]

```

即看到了/admin/login/对应的view方法: ` url(r'^login/$', self.login, name='login'),` , 通过Debug即可找到对应的html模版为/usr/local/lib/python2.7/dist-packages/django/contrib/admin/templates/admin/login.html文件.经过Django的Template渲染后,就生成了静态的html .


