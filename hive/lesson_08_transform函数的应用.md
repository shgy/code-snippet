很多时候，不可避免地会遇到如下的业务需求:
1. 从复杂的HTML中抽取目标字段(数据清洗)
2. 一个HTML文本中提取出多行目标字段.
3. 对中文文本进行分词,统计词频.
4. 识别出文本中的公司名/人名等命名实体.
....

这些需求是没有办法简单地使用Hive的SQL来搞定的. 通常会利用Python的第三方库解决这类的问题
Hive提供了streaming来满足这一需求,即使用transform.
假定有如下的数据:
```
a=1,b=3,c=4
e=2,a=3,d=5
g=10,f=8,k=7
```
需要转换成:
```
a 1
b 3
c 4
...
```
可以这样来处理:
首先编写处理数据的python脚本 kv_extract.py,然后进入到hive命令行,如下:
```
create table tab_kv(line string);
load data local inpath '/home/shgy/tmp/kv.txt' overwrite into table default.tab_kv;
add file /home/shgy/github-public/code-snippet/hive/kv_extract.py;
select transform(line) using 'python kv_extract.py' as (line,k,v) from default.tab_kv;
```

接下来任务的复杂性就由python脚本来决定了.








