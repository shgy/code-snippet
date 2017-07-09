SELECT id , pos, date_add('2017-06-01',pos) FROM default.all_emp
LATERAL VIEW posexplode(split(repeat('1,',10),',')) dd as pos, val
WHERE dt='2017-06-27' and id=1345;

用于扩展时间， 在计算新增， 累计时会有用。


