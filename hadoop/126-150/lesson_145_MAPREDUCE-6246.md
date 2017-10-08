https://issues.apache.org/jira/browse/MAPREDUCE-6246

这个bug可以说是一个 “;"引发的血案。

```
+
+    if (dbProductName.startsWith("DB2") || dbProductName.startsWith("ORACLE")) {
+      query.append(")");
+    } else {
+      query.append(");");
+    }
 
```

如果是db2， 或者 oracle 数据库， 则生成insert into语句时， 末尾不带";" 

