Django内部会自带缓存，这个缓存配置在哪里呢？在django.conf.global_settings.py中

```

CACHES = {

    'default': {

        'BACKEND': 'django.core.cache.backends.locmem.LocMemCache',

    }

}

CACHE_MIDDLEWARE_KEY_PREFIX = ''

CACHE_MIDDLEWARE_SECONDS = 600

CACHE_MIDDLEWARE_ALIAS = 'default'

```

它是线程安全的，通过阅读其源码，可以了解到它使用pickle序列化对象， 如何使用呢？

```

from django.core.cache import cache

def home(request):

        cache_val = cache.get('my_key')

        if not cache_val: 

            cache.set('my_key', 'hello, world!', 60*15)

```



Django还有自带了page_cache, 用来来缓存整个页面，默认底层使用`LocMemCache`, 但page_cache是一个Middleware,

其源码在django.middleware.cache.py文件中，它只会缓存GET/HEAD方法，而不会缓存POST方法。

不用但心动态网站的问题，因为它缓存时的key使用`request.build_absolute_uri()`方法，会同时包含请求url及参数。

```

def _generate_cache_header_key(key_prefix, request):

    """Returns a cache key for the header cache."""

    url = hashlib.md5(force_bytes(iri_to_uri(request.build_absolute_uri())))

    cache_key = 'views.decorators.cache.cache_header.%s.%s' % (

        key_prefix, url.hexdigest())

    return _i18n_cache_key_suffix(request, cache_key)

```

对于HTTP Web服务，缓存还可以通过HTTP Header来控制，这些在Django的官方文档中都有，用到了再细细研究。
