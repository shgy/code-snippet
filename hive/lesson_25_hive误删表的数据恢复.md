
```
load data inpath
'/user/guangying/.Trash/Current/apps/hive/warehouse/hcb_pushdb.db/dw_push_cargo_in_daily_dtl/dt=%s'
into table hcb_pushdb.dw_push_cargo_in_daily_dtl PARTITION (dt='%s');
```
生成脚本的sql为:
```
from datetime import datetime, timedelta

for i in range(60):
    a = datetime.strptime('2017-12-19', '%Y-%m-%d') - timedelta(days=i)
    print """
load data inpath
'/user/guangying/.Trash/Current/apps/hive/warehouse/hcb_pushdb.db/dw_push_cargo_in_daily_dtl/dt=%s'
into table hcb_pushdb.dw_push_cargo_in_daily_dtl PARTITION (dt='%s');
    """ % ( a.strftime('%Y-%m-%d'), a.strftime('%Y-%m-%d'))
```
