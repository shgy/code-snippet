# -*- coding: utf-8 -*-
from sshtunnel import SSHTunnelForwarder
import MySQLdb, time

with SSHTunnelForwarder(
        ssh_address_or_host=('42.62.111.195', 22),
        ssh_username='shuaiguangyin',
        ssh_password='shuaiguangyin',
        local_bind_address=('127.0.0.1', 3333),
        remote_bind_address=('10.0.0.20', 3306)) as server:
    time.sleep(5)
    conn = MySQLdb.connect(host='127.0.0.1',
                           port=3333,
                           user='dapuser',
                           passwd='4A5HYA5vOBJdQC7o2H1M',
                           db='dap',
                           charset="utf8")

    # 使用cursor()方法获取操作游标
    cursor = conn.cursor()

    # 使用execute方法执行SQL语句
    cursor.execute("SELECT VERSION()")

    # 使用 fetchone() 方法获取一条数据库。
    data = cursor.fetchone()

    print "Database version : %s " % data

    # 关闭数据库连接
    conn.close()