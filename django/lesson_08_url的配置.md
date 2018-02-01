Django的Url支持如下的几种配置:

直接使用`URL+VIEW` 正则表达式

```

from django.conf.urls import url

from . import views

urlpatterns = [

url(r'^articles/2003/$', views.special_case_2003),

url(r'^articles/([0-9]{4})/$', views.year_archive),

url(r'^articles/([0-9]{4})/([0-9]{2})/$', views.month_archive),

url(r'^articles/([0-9]{4})/([0-9]{2})/([0-9]+)/$', views.article_detail),

]

```

使用`URL+VIEW` 正则表达式带Name Group; Python正则表达式的语法， 不解释。

```

from django.conf.urls import url

from . import views

urlpatterns = [

url(r'^articles/2003/$', views.special_case_2003),

url(r'^articles/(?P<year>[0-9]{4})/$', views.year_archive),

url(r'^articles/(?P<year>[0-9]{4})/(?P<month>[0-9]{2})/$', views.month_archive),

url(r'^articles/(?P<year>[0-9]{4})/(?P<month>[0-9]{2})/(?P<day>[0-9]{2})/$', views.article_detail

]

```

定义view会有所不同：

```

def year_archive(request,year):

    pass

```



关于view的两种写法：

```

from django.conf.urls import url

urlpatterns = [

url(r'blog/(page-(\d+)/)?$', blog_articles),

url(r'comments/(?:page-(?P<page_number>\d+)/)?$', comments),

]

```

其区别在于：

```

>>> re.search(r'blog/(?:page-(?P<pn>\d+)/)?$','blog/page-1/').groups()

('1',)


>>> re.search(r'blog/(page-(?P<pn>\d+)/)?$','blog/page-1/').groups()

('page-1/', '1')

```

```

url('^query_(?P<prefix>aaaa|bbbb|cccc|dddd|eeee|ffff)_suffix/$', 'views.query_suffix',name='query_suffix'),

```

可以匹配:

query_aaaa_suffix/query_bbbb_suffix/query_cccc_suffix/query_dddd_suffix/query_eeee_suffix/query_ffff_suffix 这几个url.

```

def view(request, prefix):

      do_some_thing

```


