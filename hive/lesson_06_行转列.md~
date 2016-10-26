场景: 假设有如下的数据格式
```
type feature     value
家电    曝光      20
家电    点击      10
家电    购买      5
```
希望转换成如下的数据格式
```
品类  曝光      点击     购买
家电  20      10      5
```
这样转换后,方便存储到excel中,进行分析. 可以使用的方法如下
'''
    select dt, type,
      regexp_extract(concat_ws(',',collect_list(concat(feature,'_', value))), '曝光_([0-9]+)',1) imp_cnt,
      regexp_extract(concat_ws(',',collect_list(concat(feature,'_', value))), '点击_([0-9]+)',1) view_cnt,
      regexp_extract(concat_ws(',',collect_list(concat(feature,'_', value))), '购买_([0-9]+)',1) normal_cnt
    from mydb.mytable where dt='2016-09-06' group by dt, type
'''
在数据量比较少的情况下, 这种方法一个map-reduce job就完成任务了, 速度相当给力.