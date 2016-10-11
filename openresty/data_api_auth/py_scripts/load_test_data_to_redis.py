# -*- coding: utf-8 -*-
import redis, time
r = redis.Redis(host='127.0.0.1', port=6379, db=0,charset='utf-8',password='redis_passwd')

# print r.evalsha("12345678", 2, "token", int(time.time()))

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
    'api': ['/api/data1/', '/api/data2/']
}

def fake_data(ak, dat):
    cleft_val, cleft_expire = dat['cleft']['val'], dat['cleft']['expire']
    r.setex('cleft:%s' % ak, value=cleft_val, time=cleft_expire)
    r.hmset('info:%s' % ak, dat['info'])
    r.sadd('ip:%s' % ak, *dat['ip'])
    r.sadd('whiteips', *dat['ip'])
    r.sadd('api:%s' % ak, *dat['api'])

fake_data('token', dat)
