
API的权限验证需求如下：
1 在白名单中的IP才有访问系统的权限。
2 每个APP Key有访问时间的限制(sdate, edate)
3 每个APP Key有总访问次数的限制。
4 每个APP Key在一段时间里有访问次数的限制，如每天100次或者每小时100次。
5 每个APP Key对访问者的IP也有限制。(访问IP串用)
6 每个APP Key只允许访问指定的接口(uri)。
Redis的设计如下：
hash:   sdate  edate  climit  cunit  tlimit  tleft forbid。
Redis中Key的设计如下： info\: \$ak
其中，sdate edate 表示APP Key的生效时间；climit tlimit 表示单位时间限制客户访问的次数，总访问次数。  cunit表示单位时间的长度，例如cunit=3600表示1小时。tleft表示总剩余量。
forbid 表示此APP Key被禁止访问（例如，该APP Key被客户用来攻击系统）。
string:   cleft 表示单位时间里访问次数剩余量。  cleft设置expire = cunit （ 这个设计思路很重要！！）。
set:  ip  中存储当前APP Key允许访问的IP。  Redis中Key的设计如下： ip: $ak
set: uri 中存储当前APP Key允许访问的接口。 Redis中Key的设计如下： uri: $ak
假如有一个APP Key名为token , 往Redis中插入数据的样例代码如下
``` 
# -*- coding: utf-8 -*-
import redis, time
r = redis.Redis(host='127.0.0.1', port=6379, db=0,charset='utf-8',password='redispass')

# print r.evalsha("0d6b542884c7e95e2b946957dc9e67d9bd9bbac6", 2, "token", int(time.time()))

dat = {
    'cleft': {'val': 10, 'expire': 60},  # 10 count/hour
    'info': {
        'sdate': int(time.mktime(time.strptime('2016-06-21 12:00:00', '%Y-%m-%d %X'))),
        'edate': int(time.mktime(time.strptime('2016-06-22 12:00:00', '%Y-%m-%d %X'))),
        'forbid': False,
        'cunit': 60,
        'climit': 50,
        'tlimit': 1000,
        'tleft': 1000,
        'check_ip': True
    },
    'ip': ['127.0.0.1'],
    'api': ['/api/bus_02/', '/api/bus_01/']
}

def fake_data(ak, dat):
    cleft_val, cleft_expire = dat['cleft']['val'], dat['cleft']['expire']
    r.setex('cleft:%s' % ak, value=cleft_val, time=cleft_expire)
    r.hmset('info:%s' % ak, dat['info'])
    r.sadd('ip:%s' % ak, *dat['ip'])
    r.sadd('whiteips', *dat['ip'])
    r.sadd('api:%s' % ak, *dat['api'])
```



