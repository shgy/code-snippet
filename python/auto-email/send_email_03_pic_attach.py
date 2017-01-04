# -*- coding: UTF-8 -*-
'''
发送图片邮件, 图片在附件中发送

'''
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

import smtplib, os

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage


mailto_list=['to_mail_1', 'to_mail_2']
mail_host="smtp.163.com"  #设置服务器
mail_user="send_user"    #用户名
mail_pass="send_pass"   #口令
mail_postfix="163.com"  #发件箱的后缀


def send_email_with_pic(sub, content, to_list=mailto_list):
    me = "Me" + "<" + mail_user + "@" + mail_postfix + ">"
    msg = MIMEMultipart()
    msg['Subject'] = sub
    msg['From'] = me
    msg['To'] = ";".join(mailto_list)

    text = MIMEText(content, _charset='utf-8') # 有中文，　一定要设置　 _charset='utf-8'
    msg.attach(text)
    img_data = open('test.jpg', 'rb').read()
    image = MIMEImage(img_data, name=os.path.basename('test.jpg'))
    msg.attach(image)

    server = smtplib.SMTP()
    try:
        server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, to_list, msg.as_string())
    finally:
        server.close()
    return True

if __name__ == '__main__':
    if send_email_with_pic("title-数据监控-2", "发送的内容"):
        print "发送成功"
    else:
        print "发送失败"