# -*- coding: UTF-8 -*-
'''
发送html邮件

'''
# import sys
# reload(sys)
# sys.setdefaultencoding("utf-8")

import smtplib, os

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

mailto_list=['to_mail_1', 'to_mail_2']
mail_host="smtp.163.com"  #设置服务器
mail_user="send_user"    #用户名
mail_pass="send_pass"   #口令
mail_postfix="163.com"  #发件箱的后缀


def send_email_with_pic(sub, content, to_list=mailto_list):
    me = "Me" + "<" + mail_user + "@" + mail_postfix + ">"

    # Create message container - the correct MIME type is multipart/alternative.
    msg = MIMEMultipart()
    msg['Subject'] = sub
    msg['From'] = me
    msg['To'] = ";".join(mailto_list)

    html = """\
    <html>
      <head></head>
      <body>
        <p>Hi!<br>
           How are you?<br>
           Here is the <a href="https://www.python.org">link</a> you wanted.
        </p>
        <div>
           <table>
              <tr><td>H1</td><td>H2</td><td>H3</td></tr>
              <tr><td>C1</td><td>C2</td><td>C3</td></tr>
              <tr><td>D1</td><td>D2</td><td>D3</td></tr>
           </table>
        </div>
      </body>
    </html>
    """

    part1 = MIMEText(html, 'html')
    # Attach parts into message container.
    # According to RFC 2046, the last part of a multipart message, in this case
    # the HTML message, is best and preferred.
    msg.attach(part1)


    server = smtplib.SMTP()
    try:
        server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, to_list, msg.as_string())
    finally:
        server.close()
    return True

if __name__ == '__main__':
    if send_email_with_pic("title", "发送的内容"):
        print "发送成功"
    else:
        print "发送失败"