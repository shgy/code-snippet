没注意将创建vdh文件添加到git中了,有100M, 由于已经多次commit了, 因此只能使用如下的方式来删除
```
git filter-branch -f --index-filter "git rm -rf --cached --ignore-unmatch assembly" -- --all  
```
注: 大文件在assembly中
