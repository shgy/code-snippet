由于python脚本简单/灵活的特性，做测试生成测试数据时，最好使用python客户端。

Step 1：

```

./start-hbase.sh

./hbase-daemon.sh start thrift2

```

Step 2:

从https://github.com/apache/hbase/tree/master/hbase-examples/src/main/python/thrift2 得到相关的操作源码，目录结构如下：

```

├── DemoClient.py

└── hbase

    ├── constants.py

    ├── __init__.py

    ├── __init__.pyc

    ├── THBaseService.py

    ├── THBaseService.pyc

    ├── THBaseService-remote

    ├── ttypes.py

    └── ttypes.pyc

```



Step 3: 执行DemoClient.py，然后通过hbase shell 查看结果：

```

hbase(main):004:0> scan 'example'

ROW                          COLUMN+CELL                                                                    

 row1                        column=family1:qualifier1, timestamp=1452349266458, value=value1 

```

```

# -*- coding: utf-8 -*-

# Created by 'shgy' on '16-4-4'

from hbthrift2.hbase import THBaseService

from hbthrift2.hbase.ttypes import *

from thrift.transport import TTransport

from thrift.transport import TSocket

from thrift.transport import THttpClient

from thrift.protocol import TCompactProtocol



def hbase_client_factory(connect=True):

    socket = TSocket.TSocket("118.123.9.148", 9090)

    transport = TTransport.TBufferedTransport(socket)

    protocol = TCompactProtocol.TCompactProtocol(transport)

    client = THBaseService.Client(protocol)

    if connect:

        transport.open()



    return client, transport



def get_one_row(table, rowkey):

    client, transport = hbase_client_factory()

    tget = TGet(row=str(rowkey))

    return resolve(client.get(table, tget))



def resolve(result, rkOnly=False):

    assert isinstance(result, TResult)

    o = {

        'rk': result.row.decode('utf8'),

        'data': []

    }

    if not rkOnly:

        for colValue in result.columnValues:

            row = {

                'qualifier': colValue.qualifier.decode('utf8', 'ignore'),

                'family': colValue.family.decode('utf8', 'ignore'),

                'value': colValue.value.decode('utf8', 'ignore')

            }

            print colValue.qualifier.decode('utf8', 'ignore')

            o['data'].append(row)

    return o





get_one_row('table','rowkey')

```
