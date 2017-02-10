# -*- coding: utf-8 -*-
"""
 查询手机号码归属地
 curl  --get --include  'http://apis.baidu.com/apistore/mobilephoneservice/mobilephone?tel=15846530170'  -H 'apikey:b43363e60b213e170906abb0e844c3d1'

"""

# import requests
# requests.get(url='http://apis.baidu.com/apistore/mobilephoneservice/mobilephone?tel=15846530170',{''})
import urllib2, json



# mobiles_locate(['123'])

# coding=utf-8
from Tkinter import *
from ttk import *
from ScrolledText import ScrolledText
__author__ = 'Administrator'

import os
import tkMessageBox
import threading

import time, json
# import api_export

# 创建锁,并置于锁定状态

from Queue import Queue

backendQueue = Queue()
guiQueue = Queue()


class JobThread(object):

    def notice(self,msg, dtype='process'):
        dat = {"type": dtype, "data": msg}
        backendQueue.put(dat)

    def mobiles_locate(self, mobiles):

        for mobile in mobiles:
            is_mobile = isinstance(mobile, basestring) and re.match(r'^(?:(13[0-9])|(15[^4,\\d])|(18[0,0-9]))\d{8}$', mobile)
            if not is_mobile:
                yield u'%s,%s' % (mobile, u'非手机号')
                continue
            try_cnt = 3
            while try_cnt >= 0 :

                try:
                    req = urllib2.Request(
                        url='http://apis.baidu.com/apistore/mobilephoneservice/mobilephone?tel=%s' % mobile,
                        headers={'apikey': 'b43363e60b213e170906abb0e844c3d1'})
                    f = urllib2.urlopen(req)
                    dat = f.read()
                    # {"errNum": 0, "retData": {"province": "四川", "telString": "15008246568", "carrier": "四川移动"}, "errMsg": "success"}
                    dat = json.loads(dat, encoding='utf-8')
                    show_data = u'%s, %s' % (mobile, dat['retData']['province'])
                    break
                except:
                    time.sleep(1)
                    show_data = u'%s, %s' % (mobile, u'Error')
                try_cnt -= 1

            yield show_data

    def do_work(self, *args, **kwargs):
        while True:

            mobiles = guiQueue.get()

            self.notice(dtype="start", msg='query start')
            try:
                ret_list = self.mobiles_locate(mobiles)
                for each in ret_list:
                    self.notice(each)
            except Exception as e:
                self.notice(dtype='err', msg=e.message)
                time.sleep(0.5)
            self.notice(dtype='end', msg="query end ")

    def __call__(self, *args, **kwargs):
        self.do_work(*args, **kwargs)


class App(object):

    def notice(self, msg):
        self.txt_locate.insert(END, msg)
        self.txt_locate.insert(END, '\n')

    def check_status(self):
        try:
            dat = backendQueue.get(block=False)
        except:
            dat = None

        if dat is None:
            pass
        elif dat['type'] == 'start':
            pass
        elif dat['type'] == 'process':
            self.progress['value'] += 1
            self.notice(dat['data'])

        elif dat['type'] == 'end':
            # tkMessageBox.showinfo(title=u'tips', message=dat['data'])

            self.btn.state(['!disabled'])
            return

        self.txt_locate.after(100, self.check_status)

    def query(self, *args):
        if self.btn.instate(['disabled']): return
        self.btn.state(['disabled'])

        self.txt_locate.delete("1.0", 'end-1c')

        mobiles = self.txt_mobile.get("1.0", 'end-1c').strip()

        if not mobiles:
            tkMessageBox.showerror(title=u'错误', message=u'请输入手机号')
            self.btn.state(['!disabled'])
            return

        mobiles = map(lambda x: x.strip(), mobiles.strip().split('\n'))
        self.progress["value"] = 0
        self.progress["maximum"] =  len(mobiles)

        guiQueue.put(mobiles)
        self.check_status()

    def __init__(self):
        self.root = Tk()

        Label(self.root, text=u"输入手机号码").grid(row=0, column=1)
        Label(self.root, text=u"输出信息").grid(row=0, column=3)
        self.progress = Progressbar(self.root, orient="horizontal", length=100, mode="determinate")
        self.progress.grid(row=0, column=2)
        self.txt_mobile = ScrolledText(master=self.root, width=30, height=70, yscrollcommand='1')
        self.txt_mobile.grid(row=1, column=1, padx=5)
        self.btn = Button(master=self.root, text=u"开始查询")

        self.btn.grid(row=1, column=2, sticky=N, pady=150)
        self.btn.bind("<Button-1>", self.query)
        self.txt_locate = ScrolledText(master=self.root, width=35, height=70, yscrollcommand='1')
        self.txt_locate.grid(row=1, column=3, padx=5)

        jobthread = threading.Thread(target=JobThread(), kwargs={"gui": self})
        jobthread.setDaemon(True)
        jobthread.start()

app = App()
def on_closing():
    if tkMessageBox.askokcancel("Quit", u"确认退出?"):
        app.root.destroy()

app.root.protocol("WM_DELETE_WINDOW", on_closing)
app.root.title(u"电话归属地查询工具V1.1")
app.root.mainloop()