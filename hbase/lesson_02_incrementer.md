1 计数器
计数器可以方便、快速地进行计数操作，而且避免了加锁等保证了原子性的操作。

1.1 Java API 操作 HBase 计数器
```
public Result increment(final Increment increment)
public long incrementColumnValue(final byte [] row, final byte [] family, final byte [] qualifier, final long amount)
public long incrementColumnValue(final byte [] row, final byte [] family, final byte [] qualifier, final long amount, final Durability durability)
```
从这 3 个 HBase 提供的 计数器 API 来看，可以知道有 单列计数器 和 多列计数器

pv + 1 的 Java 示例如下 : 
_hTable.incrementColumnValue(Bytes.toBytes("row-zhangsan-001"), Bytes.toBytes("info"), Bytes.toBytes("pv"), 1L);

1.2 Shell 操作 HBase 计数器
```
hbase(main):011:0> incr 'user', 'row-zhangsan-001', 'cf1:pv', 10
hbase(main):012:0> incr 'user', 'row-zhangsan-001', 'cf1:pv', -1

hbase(main):013:0> scan 'user'
ROW                                                 COLUMN+CELL                                                                                                                                         
row-zhangsan-001                                   column=cf1:pv, timestamp=1438853474770, value=\x00\x00\x00\x00\x00\x00\x00\x09
hbase(main):014:0> get_counter 'user', 'row-zhangsan-001', 'cf1:pv', ''
COUNTER VALUE = 9
```
// 看下面提示，给的例子只要3个参数，为什么我要打4个才能够用？？？
```
hbase(main):015:0> get_counter 'user', 'row-zhangsan-001', 'cf1:pv'
ERROR: wrong number of arguments (3 for 4)
Here is some help for this command:
Return a counter cell value at specified table/row/column coordinates.
A cell cell should be managed with atomic increment function oh HBase
and the data should be binary encoded. Example:
  hbase> get_counter 'ns1:t1', 'r1', 'c1'
  hbase> get_counter 't1', 'r1', 'c1'
The same commands also can be run on a table reference. Suppose you had a reference
t to table 't1', the corresponding command would be:
  hbase> t.get_counter 'r1', 'c1' 


hbase(main):055:0> get 'test_icv_tmp_1', 'row-zhangsan-001', 'cf1:pv'
COLUMN                                              CELL                                                                                                                                                   
cf1:pv                                             timestamp=1438853974733, value=\x00\x00\x00\x00\x00\x00\x00\x0A                                                                                       
1 row(s) in 0.0080 seconds

hbase(main):056:0> 
```
1.3 单列计数器
_hTable.incrementColumnValue(Bytes.toBytes("row-zhangsan-001"), Bytes.toBytes("info"), Bytes.toBytes("pv"), 10L); // pv +10
_hTable.incrementColumnValue(Bytes.toBytes("row-zhangsan-001"), Bytes.toBytes("info"), Bytes.toBytes("pv"), -1L); // pv -1

1.4 多列计数器
pv +2 的同时，uv 同时 +1
Increment increment = new Increment(Bytes.toBytes("row"));
increment.addColumn(Bytes.toBytes("info"), Bytes.toBytes("pv"), 1L); // pv +2
increment.addColumn(Bytes.toBytes("info"), Bytes.toBytes("uv"), 1L); // uv +1
_hTable.increment(increment);
