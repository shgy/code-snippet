1. 安装samba
sudo apt-get install samba samba-common

2. 配置samba

[share]
path = /data1/linux-win-share
browseable = yes
writable = yes
comment = smb share test


3. 在samba中添加用户
 sudo smbpasswd -a samba_user 

第3步很重要, 如果没有配置, 会出现拒绝访问.
