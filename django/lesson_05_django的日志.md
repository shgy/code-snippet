```
# Logging

# settings.py

LOGGING = {

    'version': 1,

    'disable_existing_loggers': False,

    'formatters': {

        'verbose': {

            'format' : "[%(asctime)s] %(levelname)s [%(name)s:%(lineno)s] %(message)s",

            'datefmt' : "%d/%b/%Y %H:%M:%S"

        },

        'simple': {

            'format': '%(levelname)s %(message)s'

        },

    },

    'handlers': {

        'file': {

            'level':'DEBUG',

            'class':'logging.handlers.RotatingFileHandler',

            'filename': 'logs/mylog.log',

            'maxBytes': 1024*1024*5, # 5 MB

            'backupCount': 5,

            'formatter':'verbose',

        },

    },

    'loggers': {

        'django': {

            'handlers':['file'],

            'propagate': True,

            'level':'DEBUG',

        },

        'hello_app': {

            'handlers': ['file'],

            'level': 'DEBUG',

        },

    }

}
```
