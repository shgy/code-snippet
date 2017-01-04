# -*- coding: UTF-8 -*-
'''
发送txt文本邮件
小五义：http://www.cnblogs.com/xiaowuyi
'''
import smtplib
from email.mime.text import MIMEText

mailto_list=['to_mail_1', 'to_mail_2']
mail_host="smtp.163.com"  #设置服务器
mail_user="send_user"    #用户名
mail_pass="send_pass"   #口令
mail_postfix="163.com"  #发件箱的后缀

def send_mail(sub, content, to_list=mailto_list):

    me="Me"+"<"+mail_user+"@"+mail_postfix+">"
    msg = MIMEText(content, _subtype='plain', _charset='utf-8')
    msg['Subject'] = sub
    msg['From'] = me
    msg['To'] = ";".join(to_list)

    server = smtplib.SMTP()
    try:
        server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, to_list, msg.as_string())
    finally:
        server.close()
    return True

if __name__ == '__main__':
    if send_mail("title-数据监控", "发送的内容"):
        print "发送成功"
    else:
        print "发送失败"