Lucene的核心有两部分: 索引数据(indexing) 和 检索数据(searching).
从功能上来说, 先接触的是indexing逻辑, 也就是索引的设计, 后接触的是查询.
从业务上来说, 先解决的是searching问题, 即各类query的组合使用,indexing反而是次要的. 

Searching最基本的就是TermQuery. 所以选择从TermQuery入手, 理解Lucene的检索过程.








