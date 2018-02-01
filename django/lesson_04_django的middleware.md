django middleware类似于Servlet的filter，提供对http请求的拦截机制。通常的应用场景如下：
1 未登录用户访问网站要跳转到登录页面。
2 参数的合法性验证。

django中内置了许多middleware，在settings文件中即可看到。

middleware的开发非常简单：
1 创建middleware类，如下：

```

import json
from django.http import HttpResponse


class HelloMiddleware(object):

    def process_request(self, request):

        aa = CommonMiddleware()

        token = request.GET.get('token')

        print token

        if not token:

            return HttpResponse("errror token", content_type='application/json')

```

结构如下：

```

djangodemos/middlewares/

├── hellomiddleware.py

├── hellomiddleware.pyc

├── __init__.py

└── __init__.pyc

```

2 在settings.py中配置middleware

```

MIDDLEWARE_CLASSES = (
   'django.contrib.sessions.middleware.SessionMiddleware',
   'django.middleware.common.CommonMiddleware',
   'django.middleware.csrf.CsrfViewMiddleware',
   'django.contrib.auth.middleware.AuthenticationMiddleware',
   'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
   'django.contrib.messages.middleware.MessageMiddleware',
   'django.middleware.clickjacking.XFrameOptionsMiddleware',
   'django.middleware.security.SecurityMiddleware',
   'middlewares.hellomiddleware.HelloMiddleware'
)

```



django的middleware 类似于Java的切面编程。参考django-1.8 官方文档，midlleware一共有如下的方法



```

class ReqLimitWare(object):

   def __init__(self):
       print 'req limit ware init'

   def process_request(self, request):
       print 'process request'

   def process_view(self, request, callback, callback_args, callback_kwargs):
       print 'process view'


   def process_exception(self,request, exception):
       print 'process exception'

   def proecss_template_response(self,request, response):
       print 'process template response'
       return response

   def process_response(self, request, response):
       print 'process response'
       return response

```


