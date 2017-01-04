# -*- coding: utf-8 -*-
# 参考: http://blog.csdn.net/xw_classmate/article/details/52490354
"""
paramiko是用python语言写的一个模块，遵循SSH2协议，支持以加密和认证的方式，进行远程服务器的连接。

"""
import paramiko

def connect_by_passwd():
    paramiko.util.log_to_file('paramiko.log') # 创建SSH连接日志文件（只保留前一次连接的详细日志，以前的日志会自动被覆盖）

    ssh = paramiko.SSHClient()
    # 读取known_hosts
    ssh.load_system_host_keys()
    # 允许连接不在know_hosts文件中的主机
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    # 连接服务器
    ssh.connect(hostname='192.168.1.7', port=22, username='vita', password='vita')
    # 执行命令
    stdin, stdout, stderr = ssh.exec_command('ls')
    # 获取命令执行结果
    print stdout.read()
    # 关闭连接
    ssh.close()

def connect_by_private_key():
    paramiko.util.log_to_file('paramiko.log')  # 创建SSH连接日志文件（只保留前一次连接的详细日志，以前的日志会自动被覆盖）

    # 创建SSH对象
    ssh = paramiko.SSHClient()
    # 允许连接不在know_hosts文件中的主机
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    # 连接服务器
    ssh.connect(hostname='127.0.0.1', port=22, username='shgy', key_filename='/home/shgy/.ssh/id_dsa')
    # 执行命令
    stdin, stdout, stderr = ssh.exec_command('df')
    # 获取命令结果
    print stdout.read()
    # 关闭连接
    ssh.close()

if __name__ == '__main__':
    connect_by_passwd()