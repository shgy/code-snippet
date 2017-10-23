-print0 和 -0 配合使用

find . -name "*.sql" -print0 | xargs -0 grep "照片" 
