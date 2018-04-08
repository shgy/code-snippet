pg安装后, 默认会有如下的数据库及用户:
```
exampledb=# \l
                                  List of databases
   Name    |  Owner   | Encoding |   Collate   |    Ctype    |   Access privileges   
-----------+----------+----------+-------------+-------------+-----------------------
 exampledb | shgy     | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 postgres  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 template0 | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
           |          |          |             |             | postgres=CTc/postgres
 template1 | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
           |          |          |             |             | postgres=CTc/postgres

```
exampledb 和 shgy用户是自己创建的.

典型的操作:
1. 创建用户并设置密码
```
shgy@shgy-desktop:~$ sudo -u postgres createuser --superuser shgy

shgy@shgy-desktop:~$ sudo -u postgres psql
psql (9.5.12)
Type "help" for help.

postgres=# \password shgy
Enter new password: 
Enter it again: 
postgres=# 
```

2. 创建数据库
```
 shgy@shgy-desktop:~$ sudo -u postgres createdb -O shgy exampledb
```

3. 进入数据库
```
shgy@shgy-desktop:~$ psql -U shgy -d exampledb 
psql (9.5.12)
Type "help" for help.

exampledb=# \l
exampledb=# \l
                                  List of databases
   Name    |  Owner   | Encoding |   Collate   |    Ctype    |   Access privileges   
-----------+----------+----------+-------------+-------------+-----------------------
 exampledb | shgy     | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 postgres  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 template0 | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
           |          |          |             |             | postgres=CTc/postgres
 template1 | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/postgres          +
           |          |          |             |             | postgres=CTc/postgres
(4 rows)

exampledb=# 

```

pg使用`\l`命令查看所有的数据库, 使用`\d`命令查看所有的表.





