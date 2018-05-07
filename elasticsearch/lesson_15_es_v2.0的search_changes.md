1. partial fields
  
   使用source filtering代替了。

2. `search_type=count`废弃了
   
   搜索的时候设置size=0即可   

3. count api 内部使用search api
  
  错误抛出异常

4. 所有的存储的meta-fields 都默认返回

5. script fields 

   这个只是结果结构的优化

6. timezone for date field
  参数的标准化

7. Only highlight queried fields

   高亮的功能

8. Postings highlighter doesn't support match_phrase_prefix

  高亮的功能不再支持`match_phrase_prefix`

高亮这个在搜索引擎中使用挺常见的。


搜索已经是比较成熟的模块了， 基本上不会伤筋动骨了
