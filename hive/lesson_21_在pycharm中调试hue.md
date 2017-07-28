
首先感谢分享的博客
编译环境： ubuntu14.04

step1 : 下载并编译代码
```
$ git clone https://github.com/cloudera/hue.git
$ git checkout release-3.11.0
$ cd hue
$ pwd
/home/shgy/hcb_work/hue
$ make apps
```
当然在编译之前， 安装各种lib, 如果下载出错， 403,404啥的, 多把 make apps重复一下就OK了。

step2: 将项目导入到pycharm中: [File] -> [Open] -> (/home/shgy/hcb_work/hue)

step3: 配置项目的依赖环境: [File] -> [Settings] -> [Project] -> [Project Interpreter]
     参考图片 lesson_21_hue_settings.png

step4: 配置Django的依赖环境:  [File] -> [Settings] -> [Languages & Frameworks] -> [Django]
    参考图片 lesson_21_django_settings.png

step5: 配置运行参数: [Run] -> [Edit Configurations]
    参考图片 lesson_21_run_config.png
DESKTOP_DB_CONFIG='django.db.backends.mysql:hue::root:root123:localhost'

step 6: 修改hue的代码 appmanager.py 第60行， 添加`tb = sys.exc_traceback`
```
  try:
    __import__(module)
    return sys.modules[module]
  except ImportError, ie:
    # If the exception came from us importing, we want to just
    # return None. We need to inspect the stack, though, so we properly
    # reraise in the case that the module we're importing triggered
    # an import error itself.
    tb = sys.exc_info()[2]
    tb = sys.exc_traceback
    top_frame = traceback.extract_tb(tb)[-1]
    err_file = re.sub(r'\.pyc','.py', top_frame[0])
    my_file = re.sub(r'\.pyc','.py', __file__)
    if err_file == my_file:
      return None
    else:
      LOG.error("Failed to import '%s'" % (module,))
      raise
```
  这里引发出一个问题:
  这段代码， 在pycharm中， run 和 debug有区别
 ```
 import sys, traceback
import pdb
print ('main')
try:
    index = __import__('index')
except ImportError, ie:
    # pdb.set_trace()
    # traceback.print_exc(file=sys.stderr)
    tb = sys.exc_info()
    print sys.exc_traceback
    traceback.print_tb(tb[2], limit=1, file=sys.stderr)
    print type(tb[2])
    print type(ie)
 ```

区别在于 `sys.exc_info()`返回的tuple中， 最后一个值在debug时是None值， 在run时是 traceback对象。
Why ?  I don't Know!!

这样就可以方便地调试hue了。


参考: http://swifter.love/2017/06/01/PyCharm%E8%B0%83%E8%AF%95Hue%E6%95%99%E7%A8%8B/