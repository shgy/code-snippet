# -*- coding: UTF-8 -*-
'''
发送图片邮件, 图片在正文中
"""
163 的邮箱貌似不允许 html正文中发送图片
"""
'''
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

import smtplib, os

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from email.mime.base import MIMEBase
from email import encoders


mailto_list=['to_mail_1', 'to_mail_2']
mail_host="smtp.163.com"  #设置服务器
mail_user="send_user"    #用户名
mail_pass="send_pass"   #口令
mail_postfix="163.com"  #发件箱的后缀



def send_email_with_pic(sub, content, to_list=mailto_list):
    me = "Me" + "<" + mail_user + "@" + mail_postfix + ">"

    # Create message container - the correct MIME type is multipart/alternative.
    outer = MIMEMultipart('alternative')
    outer['Subject'] = sub
    outer['From'] = me
    outer['To'] = ";".join(mailto_list)

    outer.attach(MIMEText('<html><body><h1>Hello</h1>' +
                        '<p> <img src="cid:0"> </p>' +
                        '</body></html>', 'html'))
    with open('test.png', 'rb') as f:
        # 设置附件的MIME和文件名，这里是png类型:
        mime = MIMEBase('image', 'png', filename='test.png')
        # 加上必要的头信息:
        mime.add_header('Content-Disposition', 'attachment', filename='test.png')
        mime.add_header('Content-ID', '<0>')
        mime.add_header('X-Attachment-Id', '0')
        # 把附件的内容读进来:
        mime.set_payload(f.read())
        # 用Base64编码:
        encoders.encode_base64(mime)
        # 添加到MIMEMultipart:
        outer.attach(mime)

    server = smtplib.SMTP(mail_host,25)
    server.set_debuglevel(1)
    try:
        # server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, to_list, outer.as_string())
    finally:
        server.close()
    return True

if __name__ == '__main__':
    if send_email_with_pic("data monitor", "发送的内容"):
        print "发送成功"
    else:
        print "发送失败"