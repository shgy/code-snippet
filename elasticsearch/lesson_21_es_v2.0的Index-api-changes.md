1. index alias

alias filter中使用的Fields不必在alias创建时就存在于mapping中， 但是必须存在于mapping中。
也就是说， 可以先创建alias， 再创建filter.

2. File based index templates

Index templates不再支持在配置文件中配置， 而是使用`_template`API代替。

3. Analyze API changes

first token 下标从0开始，而不是1. `prefer_local`参数被移除。 `_analyze` API是一个轻量级操作， 
调用者不用担心具体的服务端代码在那个节点执行。 

`id_cache` 从clear cache api移除
clear cache API不再支持`id_cache`操作。 取而代之的是`fielddata`选项被用于从`_parent`域清除缓存。






