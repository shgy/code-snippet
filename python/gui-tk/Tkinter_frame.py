# -*- coding: utf-8 -*-
"""
     Tkinter教程之Frame篇

     Frame就是屏幕上的一块矩形区域，多是用来作为容器（container）来布局窗体。

     代码来源: http://blog.csdn.net/aa1049372051/article/details/51881307
"""

from Tkinter import *
root = Tk()
#以不同的颜色区别各个frame
for fm in ['red','blue','yellow','green','white','black']:
    #注意这个创建Frame的方法与其它创建控件的方法不同，第一个参数不是root
    Frame(height = 40,width = 400,bg = fm).pack()
root.mainloop()