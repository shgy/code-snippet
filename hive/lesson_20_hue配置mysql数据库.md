1. 在desktop/conf/pseudo-distributed.ini文件中配置好mysql的地址
2.
```
export DESKTOP_DB_CONFIG='django.db.backends.mysql:hue::root:root123:localhost'
./build/env/bin/hue syncdb
./build/env/bin/hue migrate
```

注意：设置系统变量要用export命令， 这样 python的os.getenv()才能取到值。