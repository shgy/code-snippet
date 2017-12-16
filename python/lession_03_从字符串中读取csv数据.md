python 的csv模式不支持unicode的数据，因此处理方式如下:
```
from io import BytesIO
s = BytesIO()
s.write(bytearray(u"中文,测试", encoding ="utf8"))
s.seek(0)
import csv
for row in csv.reader(s):
    print row
```
