v2.0重写了整个parent/child的逻辑， 降低了内存的使用。而且在进行`has_child`和`has_parent`查询是更快，更高效。`_parent`域默认会使用doc values.

这是因为 `parent-child` ID maps需要存储到`Doc Values`中去。

1. parent type不能在child type创建之前存在，但是可以在同一个mapping中创建。

2. `top_children` query 移除 使用`has_child` query 代替

   `top_children` 并不比`has_child`快， 也并不比它更精确。


关于`Parent-Child Relationship`, 可以参考`https://www.elastic.co/guide/en/elasticsearch/guide/current/parent-child.html`

