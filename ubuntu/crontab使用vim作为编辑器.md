crontab -e 使用vim作编辑器时, 不能使用 vim-basic, 要使用 vim-gnome
使用select-editor命令就可以选择.
update-alternatives --config vim

crontab的数据存储在: `/var/spool/cron/crontabs/shgy`文件中, 需要root权限才能访问.

