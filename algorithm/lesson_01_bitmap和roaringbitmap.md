所谓Bitmap就是用一个bit位来标记某个元素：{1,3,4,5,6,7,8,10} 8个数， 使用bitmap存储，
0,1,0,1,1,1,1,1,1,0,1 (画图)

从存储空间来看， 这样存储的好处在于大大节省了存储空间， 对于{1,3,4,5,6,7,8,10}原本需要32个byte存储, 这里压缩到了2个byte。
从集合的操作来看：
0. 节省存储空间
1. 排序， bitmap是有序的。
2. 查找元素是否存在， o(1)的复杂度。
3. 集合运算，bit位计算即可，不需要比较和移位，运算效率高。
 
Bitmap的使用场景很广泛， 比如说 Oracle、Redis 中都有用到 BitMap

Bitmap对于稀疏的数据集合， Bitmap反而不能节约空间。 RoaringBitmap



1. 数据不能重复
2. 稀疏数据消耗空间





参考:
https://cloud.tencent.com/developer/article/1006113
https://blog.csdn.net/qll125596718/article/details/6905476
https://www.cnblogs.com/LBSer/p/3322630.html
http://blog.51cto.com/zengzhaozheng/1404108
https://blog.csdn.net/hguisu/article/details/7880288
