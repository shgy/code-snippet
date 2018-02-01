```

from django.http import StreamingHttpResponse, HttpResponse



def textgenerator():

    for i in xrange(1,1000000000):

        yield "Hello, World\n"



def download(request):



    print 'start download----'

    clientSystem = request.META['HTTP_USER_AGENT']

    # 如果是windows则按cp936编码,否则按utf-8编码

    filename = u'中文文件名'

    if clientSystem.find('Windows') > -1:

        filename = filename.encode('cp936')

    else:

        filename = filename.encode('utf-8')



    response = StreamingHttpResponse(textgenerator(),

                   content_type="text/csv")

    response['Content-Disposition'] = 'attachment;filename=%s.csv' % filename

    return response

```

Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。当 Internet Explorer 接收到头时，它会激活文件下载对话框，它的文件名框自动填充了头中指定的文件名。（请注意，这是设计导致的；无法使用此功能将文档保存到用户的计算机上，而不向用户询问保存位置。） 服务端向客户端游览器发送文件时，如果是浏览器支持的文件类型，一般会默认使用浏览器打开，比如txt、jpg等，会直接在浏览器中显示，如果需要提示用户保存，就要利用Content-Disposition进行一下处理，关键在于一定要加上attachment： Response.AppendHeader("Content-Disposition","attachment;filename=FileName.txt");



这样的做法导致的结果是每条数据一个Http请求，而非批量下载。如果希望达到批量下载的效果，有如下的办法：

# 1 使用FileWrapper

```

# -*- coding:utf-8 -*-

from django.http import StreamingHttpResponse

from django.core.servers.basehttp import FileWrapper

import csv

from StringIO import StringIO



def textgenerator():

    for i in xrange(1,10000000):

        yield ["hello", "world"]





class Batch:

    def __init__(self):

        self.producer = textgenerator()



    def read(self, size):

        sa = StringIO()

        writer = csv.writer(sa)

        try:

            [writer.writerow(self.producer.next()) for i in range(size)]

            return sa.getvalue()

        except StopIteration:

            return None

        finally:

            sa.close()





def download(request):



    print 'start download----'



    clientSystem = request.META['HTTP_USER_AGENT']



    # 如果是windows则按cp936编码,否则按utf-8编码

    filename = u'中文文件名'

    filename = filename.encode('cp936') if clientSystem.find('Windows') > -1 else filename.encode('utf-8')

    wrapper = FileWrapper(Batch(), blksize=10)

    response = StreamingHttpResponse(wrapper, content_type="text/csv")

    response['Content-Disposition'] = 'attachment;filename=%s.csv' % filename



    return response

```

# 2 yield更多的数据

Django的StreamingHttpResponse应该是以yield为断点。即每个yield为一批数据。所以yield控制写入更多的数据即可。

```

def textgenerator():

    for i in xrange(1, 10000000):

        dat = []

        for j in range(10):

            dat.append("aaa %s" % j)

        yield ','.join(dat)

```


