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
from email.mime.base import MIMEBase
from email import encoders
from email import Header

mailto_list=['to_mail_1', 'to_mail_2']
mailcc_list=['to_mail_1', 'to_mail_2']
mail_host="smtp.163.com"  #设置服务器
mail_user="send_user"    #用户名
mail_pass="send_pass"   #口令
mail_postfix="163.com"  #发件箱的后缀

import xlwt, xlrd, StringIO, re

def make_excels():

    wb = xlwt.Workbook()
    sheet = wb.add_sheet(u"shee名称")

    sheet.write(1, 2, 1024)
    # 参考 https://github.com/python-excel/xlwt/blob/master/examples/formulas.py
    sheet.write(6, 3, xlwt.Formula("SUM($A$1:$C$5)")) # 公式
    out = StringIO.StringIO()
    wb.save(out)

    return [(u'excel名称', out.getvalue())]


def send_email_with_excel_attch():
    me = "Me" + "<" + mail_user + "@" + mail_postfix + ">"
    msg = MIMEMultipart()
    msg['Subject'] = u"邮件主题"
    msg['From'] =  (u"%s<%s>") % (Header(u'中文名字 ','utf-8'), mail_user) # 如果不这样写, 会中文乱码
    msg['To'] = ";".join(mailto_list)
    msg['Cc'] =  ";".join(mailcc_list) # 抄送

    text = MIMEText(u"积分运营最近一周数据, 数据请见附件", _charset='utf-8')
    msg.attach(text)

    excels = make_excels()
    for excel in excels:
        name, content = excel
        attch1 = MIMEBase('application', 'octet-stream')
        attch1.set_payload(content)
        # # Encode the payload using Base64
        encoders.encode_base64(attch1)
        attch1['Content-Disposition'] = 'attachment; filename=%s' % name
        msg.attach(attch1)

    server = smtplib.SMTP()
    try:
        server.connect(mail_host)
        server.login(mail_user, mail_pass)
        server.sendmail(me, mailto_list, msg.as_string())
    finally:
        server.close()
    return True

if __name__ == '__main__':
    if send_email_with_excel_attch():
        print "发送成功"
    else:
        print "发送失败"

# # 构造附件1，传送当前目录下的 test.txt 文件
# att1 = MIMEText(open('test.txt', 'rb').read(), 'base64', 'utf-8')
# att1["Content-Type"] = 'application/octet-stream'
# # 这里的filename可以任意写，写什么名字，邮件中显示什么名字
# att1["Content-Disposition"] = 'attachment; filename="test.txt"'
# message.attach(att1)