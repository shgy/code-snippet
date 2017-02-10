# -*- coding: utf-8 -*-
from Tkinter import *
from ttk import *
from ScrolledText import ScrolledText


master = Tk()
Label(master, text=u"输入").grid(row=0, column=1)
Label(master, text=u"输出").grid(row=0, column=3)
txt_mobile= ScrolledText(master=master, width=30, height=70, yscrollcommand='1')
txt_mobile.grid(row=1, column=1)
btn = Button(master=master,  text=u"开始查询")

btn.grid(row=1, column=2, sticky=N, pady=150)


txt_locate = ScrolledText(master=master, width=40, height=70, yscrollcommand='1')
txt_locate.grid(row=1, column=3)

mainloop()