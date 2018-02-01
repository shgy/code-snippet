Django启动后会打印出

```

April 19, 2016 - 09:34:45

Django version 1.9.2, using settings 'website.settings'

Starting development server at http://0.0.0.0:8000/

Quit the server with CONTROL-C.

```

在runserver.py中代码如下：

```

        self.stdout.write((

            "Django version %(version)s, using settings %(settings)r\n"

            "Starting development server at http://%(addr)s:%(port)s/\n"

            "Quit the server with %(quit_command)s.\n"

        ) % {

            "version": self.get_version(),

            "settings": settings.SETTINGS_MODULE,

            "addr": '[%s]' % self.addr if self._raw_ipv6 else self.addr,

            "port": self.port,

            "quit_command": quit_command,

        })

```

如果希望在Django启动时做一些事情， 那么可以在自定义app的apps.py中实现AppConfig.ready()方法。

```

class ApiConfig(AppConfig):

    name = 'api'

```

关于AppConfig的功能, 这个文章讲得很详细: 

https://mozillazg.com/2015/07/django-how-to-change-app-name-and-sort-app-on-admin-site.html

Django在启动的过程中会调用ready()方法。

Django使用select实现非阻塞IO, 在线程中每0.5秒重试一次(设置select.select的timeout=0.5)。


