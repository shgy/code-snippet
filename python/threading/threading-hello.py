# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

import threading, time

class JobThread(object):


    def __call__(*args, **kwargs):

        print 'call job'
        time.sleep(1)
        print 'finish'

t = threading.Thread(target=JobThread(), kwargs={})
t.setDaemon(True)
t.start()

t.join()
