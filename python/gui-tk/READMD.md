开发一个小工具需要用到GUI, 知乎上推荐使用Tk.然后看到Python的官方文档library中介绍的就是Tk.觉得那就是它了.
由于功能简单, 很快就开发完了. 最后使用命令`pyinstaller -w --one xx.py`打包成pe/elf文件就完成整个工具
(界面参考data-export-tool-gui.png文件).

Tk是一个跨平台的窗口工具包, 至少在windows/ubuntu平台可以正常运行. 其特点在于简单易用.
在桌面应用式微的今天, 使用Tk做一些简单的GUI程序足以应付日常的需求.

在Python中, 分别有Tkinter/ttk/tix三个库用于GUI的开发.
其中 Tkinter 是对Tk GUI工具包的封装；ttk是Tkinter的加强版；tix是ttk的加强版.

关于Tk的知识, python library有比较充分的介绍, 但是几乎没有代码样例.
我是通过结合library文档及各个零散的博客来学习如何开发GUI工具. 正因为如此, 我意识到将代码片断整合起来是一件很重要的事情.
大部分时候, 一个简单的例子比长篇大论要来得有用得多.因此决定开启code-snippet这个项目. 不限语言,不限主题.
唯一的要求是代码要尽量简短. 说明要尽量详尽.
