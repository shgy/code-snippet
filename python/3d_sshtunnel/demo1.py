# -*- coding: utf-8 -*-
"""
网络场景:
   服务器A上部署了一台Webserver, 但是该机器对外网只开放了22端口.
   如何在服务器B上访问该Webserver呢?

   启动一台虚拟机A,网络使用桥接模式, 假设IP为: 192.168.1.7
   可以使用Django来模拟这一场景, 启动命令为: python manage.py runserver. 这样Django的服务只能本机访问.

   然后在本机使用ssh命令建立端口转发: ssh -fN -L  8001:localhost:8000 vita@192.168.1.7
   这样在本机使用localhost:8001即可访问虚拟机A中的web服务了.

   使用python的sshtunnel, 也可以实现与命令ssh -fN -L  8001:localhost:8000 vita@192.168.1.7 一样的功能.
"""
import time
from sshtunnel import SSHTunnelForwarder

server = SSHTunnelForwarder(
    ('42.62.111.195',22),
    ssh_username="shuaiguangyin",
    ssh_password="shuaiguangyin",
    remote_bind_address=('10.0.0.20', 3306),
    local_bind_address=('127.0.0.1', 3333)
)

server.start()


# work with `SECRET SERVICE` through `server.local_bind_port`.
# import requests
# print requests.get('http://localhost:8001').content

# server.stop()
