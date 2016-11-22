Hive通过antlr将SQL语句解析成AST(Abstract Syntax Tree), 之后就进入了AST分析的阶段了.

首先根据AST的TOK确定不同的SemanticAnalyzer, 比如:
explain使用ExplainSemanticAnalyzer
show database 使用 DDLSemanticAnalyzer

select 使用 CalcitePlanner 或者 SemanticAnalyzer  (set hive.cbo.enable=false;)

SHOW CONF "hive.execution.engine"

解析AST是一件很细致的活儿. 比如: 从AST中解析出table_name, 从AST中解析出字段, 从AST中识别出UDF.

