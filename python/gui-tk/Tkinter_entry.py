# -*- coding: utf-8 -*-
"""

Entry 类似于html 中的 input[type='text'], 即单行文本框.

程序演示: 在文本框中输入任意字符串,然后回车, 即可看到控制台输出的结果.

"""
from Tkinter import *
class App(Frame):
    def __init__(self, master=None):
        Frame.__init__(self, master)
        self.pack()

        self.entrythingy = Entry()
        self.entrythingy.pack()

        # here is the application variable
        self.contents = StringVar()
        # set it to some value
        self.contents.set("this is a variable")
        # tell the entry widget to watch this variable
        self.entrythingy["textvariable"] = self.contents

        # and here we get a callback when the user hits return.
        # we will have the program print out the value of the
        # application variable when the user hits return
        self.entrythingy.bind('<Key-Return>',
                              self.print_contents)

    def print_contents(self, event):
        print "hi. contents of entry is now ---->", \
              self.contents.get()

root = Tk()  # The Tk class is meant to be instantiated only once in an application.
app = App(master=root)
app.mainloop()