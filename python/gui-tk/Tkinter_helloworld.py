# -*- coding: utf-8 -*-
"""
本代码是 python library文档中的Hello World程序.

麻雀虽小, 五脏俱全.

如果已经有简单的GUI程序开发经验,比如html/css, bootstrap, java的awt/swing; 或者eclipse的swt/jface
那么理解起来就容易多了, 它们有很多相通的地方.

Tkinter的所有可视化部件都有一个共同的名字Widget, 其中Tk是最顶层的widget.
本程序用到了3个widget, 分别是: Tk, Frame, Button
如果使用HTML中的标签类比, 那么
Tk -- body
Frame -- div    ; 它们都是容器
Button -- input[type='button']

可能到这里, 会有个疑问: Tkinter中都有哪些Widget呢?



GUI程序设计无法绕开的一个主题就是布局方式(Geometry manager). Tkinter一共有3种布局方式: pack/grid/place, 本程序用到的是pack方式.

self.hi_there.pack({"side": "left"})

pack 默认是从上到下排列, 设置了参数 side="left"后, 则变成了水平排列. 自己修改参数, 运行代码即可看到效果 .

"""
from Tkinter import *

class Application(Frame):
    def say_hi(self):
        print "hi there, everyone!"

    def createWidgets(self):
        self.QUIT = Button(self)
        self.QUIT["text"] = "QUIT"
        self.QUIT["fg"]   = "red"
        self.QUIT["command"] =  self.quit

        self.QUIT.pack({"side": "left"})

        self.hi_there = Button(self)
        self.hi_there["text"] = "Hello",
        self.hi_there["command"] = self.say_hi

        self.hi_there.pack({"side": "left"})

    def __init__(self, master=None):
        Frame.__init__(self, master)
        self.pack()
        self.createWidgets()

root = Tk()  # The Tk class is meant to be instantiated only once in an application.
app = Application(master=root)
app.mainloop()
# root.destroy() 这句代码是不需要的, 不然会报异常. "application has been destroyed"