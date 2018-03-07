问题是这样的: 
运行了一个django web程序, 里面有自动调度, 每天早晨开始在新的线程中自动调度.
调度的过程如下:
1. 查询MySQL, 获取调度任务
2. 开始调度任务
3. 调度完成后, 将结果存储到MySQL中.

前面的两步都没有问题, 第三步执行出现了:
```
OperationalError: (2006, 'MySQL server has gone away')
    self.connection.autocommit(autocommit)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/backends/mysql/base.py", line 301, in _set_autocommit
    six.reraise(dj_exc_type, dj_exc_value, traceback)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/utils.py", line 97, in __exit__
    self.connection.autocommit(autocommit)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/backends/mysql/base.py", line 301, in _set_autocommit
    self._set_autocommit(autocommit)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/backends/base/base.py", line 295, in set_autocommit
    connection.set_autocommit(False)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/transaction.py", line 193, in __enter__
    with transaction.atomic(using=using, savepoint=False):
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/models/base.py", line 759, in save_base
    force_update=force_update, update_fields=update_fields)
  File "/data2/shgy/autoemail/venv/local/lib/python2.7/site-packages/django/db/models/base.py", line 734, in save
    timeline.save()

```

疑问: 如果是MySQL的连接没有了, 为啥查询的时候正常, 存储的时候报错? 仅仅是因为不在同一个线程的原因?

问题排查: 
1. 查看django源码, 看报错的调用栈信息. 对比: query和save两个操作的不同点.
2. 提出解决方案
3. 代码实现, 修复问题


首先, 我这边是直接引用了Django的models模块, 并不是在http请求中. stackoverflow果然是程序员神器.

这个问题是mysql经典的8小时问题. 如何复现这个问题呢?

修改mysql的连接参数:
```
mysql> show variables like '%timeout%';
+-----------------------------+----------+
| Variable_name               | Value    |
+-----------------------------+----------+
| connect_timeout             | 10       |
| delayed_insert_timeout      | 300      |
| have_statement_timeout      | YES      |
| innodb_flush_log_at_timeout | 1        |
| innodb_lock_wait_timeout    | 50       |
| innodb_rollback_on_timeout  | OFF      |
| interactive_timeout         | 30       |
| lock_wait_timeout           | 31536000 |
| net_read_timeout            | 30       |
| net_write_timeout           | 60       |
| rpl_stop_slave_timeout      | 31536000 |
| slave_net_timeout           | 60       |
| wait_timeout                | 30       |
+-----------------------------+----------+
13 rows in set (0.01 sec)

set wait_timeout=30;
set global wait_timeout=30;
set interactive_timeout=30;
set global interactive_timeout=30;

然后启动django服务,在mysql中使用命令:
 show processlist;
+-----+------+-----------+-----------+---------+------+----------+------------------+
| Id  | User | Host      | db        | Command | Time | State    | Info             |
+-----+------+-----------+-----------+---------+------+----------+------------------+
| 217 | root | localhost | autoemail | Query   |    0 | starting | show processlist |
+-----+------+-----------+-----------+---------+------+----------+------------------+
1 row in set (0.00 sec)
```

在save前休眠60秒, 让mysql收回connection
``` 
from django.conf import settings
import config

if not settings.configured:
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
        },
        USE_TZ=True,
        TIME_ZONE='Asia/Shanghai'
    )


from emailtask.models import AutoEmailSendTimeline

import time
timeline = AutoEmailSendTimeline.objects.get(id=1)
print timeline.is_manual
timeline.is_manual = not timeline.is_manual

time.sleep(60)
timeline.save()

print timeline.is_manual
```
就能复现出问题了.

解决方案就是stackoverflow中的 `close_old_connections()`

对于django的源码, 任需要再熟悉才行.

参考文档:
https://stackoverflow.com/questions/38041522/mysql-has-gone-away-with-django-orm-used-outside-django-project?rq=1
https://www.jianshu.com/p/69dcae4454b3
http://ju.outofmemory.cn/entry/92417
