有时候， 我们希望只使用django的orm 模块的功能。
```
# -*- coding: utf-8 -*-
from django.conf import settings
import config
settings.configure(
    DATABASES={
        'default': {
            'ENGINE': 'django.db.backends.mysql',
            'NAME': config.DB_NAME,
            'USER': config.DB_USER,
            'PASSWORD': config.DB_PASSWORD,
            'HOST': config.DB_HOST,
            'PORT': config.DB_PORT
        }
    }
)

from myapp.models import *

print list(MyModel.objects.all())
```
