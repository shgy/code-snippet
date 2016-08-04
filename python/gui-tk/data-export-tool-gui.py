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

    def do_work(self, *args, **kwargs):
        while True:

            dat = guiQueue.get()
            dtype, fmt, out = dat

            self.notice(dtype="start", msg='start download job: %s, %s, %s' % (dtype, fmt, out))
            try:
                time.sleep(5)
                # api_export.process(dtype=dtype, fmt=fmt, out=out, notice_func=self.notice)
            except Exception as e:
                self.notice(dtype='err', msg=e.message)
            time.sleep(0.5)
            self.notice(dtype='end', msg="download job finished")

    def __call__(self, *args, **kwargs):
        self.do_work(*args, **kwargs)

class App(object):
    def notice(self, msg):
        self.txt.insert(END, msg)
        self.txt.insert(END, '\n')

    def check_status(self):
        try:
            dat = backendQueue.get(block=False)
        except:
            dat = None

        if not dat:
            pass
        elif dat['type'] == 'process' or dat['type'] == 'start':
            self.notice(dat['data'])
        elif dat['type'] == 'end':
            tkMessageBox.showinfo(title=u'tips', message=dat['data'])
            self.btn.state(['!disabled'])
        elif dat['type'] == 'err':
            tkMessageBox.showerror(title=u'错误', message=dat['data'])

        self.txt.after(100, self.check_status)

    def download(self, *args):
        if self.btn.instate(['disabled']): return
        self.btn.state(['disabled'])
        self.txt.delete(1.0, END)
        dtype = self.dtype.get()
        fmt = self.fmt.get()
        out = self.ent.get()

        if not out:
            tkMessageBox.showerror(title=u'错误', message=u'输出文件必须填写')
            self.btn.state(['!disabled'])
            return

        if os.path.exists(out):
            tkMessageBox.showerror(title=u'错误', message=u'文件已经存在')
            self.btn.state(['!disabled'])
            return

        guiQueue.put([dtype, fmt, out])
        self.check_status()

    def __init__(self):
        self.root = Tk()
        # self.root.geometry('600x300')
        headFrame = Frame(height = 20,width = 700, master=self.root)
        # root.resizable(width=False, height=False) #宽不可变, 高可变,默认为True
        self.dtype = Combobox(headFrame, textvariable=StringVar(),width=10)
        self.dtype["values"] = ('dtype1', 'dtype2') # api_export.conf.dtypes()
        self.dtype["state"] = "readonly"
        self.dtype.current(0)
        # players.set("演员表")
        # print(players.get())
        self.fmt = Combobox(headFrame, textvariable=StringVar(),width=10)
        self.fmt["values"] = ('fmt1', 'fmt2') #api_export.conf.fmts()
        self.fmt["state"] = "readonly"
        self.fmt.current(0)

        l1 = Label(master=headFrame, text=u"数据类型")
        l1.pack(side='left')
        self.dtype.pack(side='left')

        l2 = Label(master=headFrame, text=u"输出格式")
        l2.pack(side='left')
        self.fmt.pack(side='left')

        l3 = Label(master=headFrame, text=u"输出文件")
        l3.pack(side='left')

        self.ent = Entry(master=headFrame)
        self.ent.pack(side='left')
        self.btn = Button(master=headFrame, text=u"开始下载")
        self.btn.pack(side='left')
        self.btn.bind("<Button-1>", self.download)

        headFrame.pack(padx='0.5c', pady='0.5c')
        self.txt= ScrolledText(master=self.root,yscrollcommand='1')
        self.txt.pack(expand=YES, fill="both",padx='0.5c', pady='0.5c')

        jobthread = threading.Thread(target=JobThread(), kwargs={"gui": self})
        jobthread.setDaemon(True)
        jobthread.start()

app = App()
def on_closing():
    if tkMessageBox.askokcancel("Quit", "Do you want to quit?"):
        app.root.destroy()

app.root.protocol("WM_DELETE_WINDOW", on_closing)
app.root.title(u"数据导出工具V1.1")
app.root.mainloop()