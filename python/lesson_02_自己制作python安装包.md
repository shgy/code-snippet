假设需要打包的程序在src目录下， 目录结构如下：
```
$ tree .
.
├── setup.py
└── src
    └── demo
        └── __init__.py

$ cat setup.py
#!/usr/bin/env python
#-*- coding:utf-8 -*-

from setuptools import setup, find_packages

setup(
      name="demo",
      version="0.1.0",
      packages=find_packages(where='src'),
      package_dir={'': 'src'},
      zip_safe=False,

      description="egg test demo.",
      long_description="egg test demo, haha.",
      author="amoblin",
      author_email = "amoblin@ossxp.com",

      license = "GPL",
      keywords = ("test", "egg"),
      platforms = "Independant",
      url = ""
    )

```
```
python setup.py bdist_egg
python setup.py install
```

需求： 如何创建出可以直接执行的安装包呢？
这就是entry_points的功能了。

```
$ tree .
.
├── setup.py
└── src
    └── demo
        ├── __init__.py
        └── __main__.py

2 directories, 3 files

$ cat setup.py
#!/usr/bin/env python
#-*- coding:utf-8 -*-

from setuptools import setup, find_packages

setup(
      name="demo",
      version="0.1.0",
      packages=find_packages(where='src'),
      package_dir={'': 'src'},
      entry_points = {
           'console_scripts':[
                 'demo = demo.__main__:main'
               ]
          },
      zip_safe=False,

      description="egg test demo.",
      long_description="egg test demo, haha.",
      author="amoblin",
      author_email = "amoblin@ossxp.com",

      license = "GPL",
      keywords = ("test", "egg"),
      platforms = "Independant",
      url = ""
    )

$ cat src/demo/__main__.py
#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys

def main(args=None):
    if args is None:
        args = sys.argv[1:]
    print('This is the main routine.')
    print('It should do something intersting.')

if __name__ == '__main__':
  main()

$ sudo python setup.py install
```
执行demo命令
```
$ demo
This is the main routine.
It should do something intersting.
```
卸载
```
$ pip uninstall demo
```

在python中， 如何调用命令呢？
```
from pkg_resources import load_entry_point
load_entry_point('demo', 'console_scripts', 'demo')()
```
load_entry_point的3个参数
```
entry_points = {
           'console_scripts':[
                 'demo = demo.__main__:main'
               ]
          },
```
第一个代表包名称： demo
第二个： 'console_scripts'
第三个： 'demo' 是 console_scripts中的key.

参考：
https://chriswarrick.com/blog/2014/09/15/python-apps-the-right-way-entry_points-and-scripts/
http://blog.csdn.net/pfm685757/article/details/48651389
