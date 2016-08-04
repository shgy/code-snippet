# -*- coding: utf-8 -*-
import threading
import time

num = 0
lock = threading.Lock()

def func(st):
    global num
    print (threading.currentThread().getName() + ' try to acquire the lock')
    if lock.acquire():
        print (threading.currentThread().getName() + ' acquire the lock.' )
        print (threading.currentThread().getName() +" :%s" % str(num) )
        num += 1
        time.sleep(st)
        print (threading.currentThread().getName() + ' release the lock.'  )
        lock.release()

t1 = threading.Thread(target=func, args=(8,))
t2 = threading.Thread(target=func, args=(4,))
t3 = threading.Thread(target=func, args=(2,))
t1.start()
t2.start()
t3.start()
