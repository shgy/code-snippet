# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import smtplib, os

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.header import Header
from email.mime.base import MIMEBase
from email import  encoders

mail_host="smtp.exmail.qq.com"  #设置服务器
mail_user=""    #用户名
mail_pass=""   # 口令
mail_postfix="qq.com"  #发件箱的后缀

class DefaultEmail:

    def __init__(self, subject, mailto_list, mailcc_list=[]):
        msg = MIMEMultipart()
        msg["Content-type"] = "text/plain; charset=utf-8";
        msg['Subject'] = subject
        msg['From'] = (u"%s<%s>") % (Header(u'Show Name', 'utf-8'), mail_user)
        msg['To'] = ";".join(mailto_list)
        msg['Cc'] = ";".join(mailcc_list)
        self.msg = msg
        self.mailto_list = mailto_list + mailcc_list
        self.attach_count = 1
        self.picture_count = 1

    def add_content(self, content, c_type='plain'):
        """
        添加文本, 例如: text, html
        :param content:
        :param c_type:
        :return:
        """
        part1 = MIMEText(content, c_type, _charset='utf-8')
        self.msg.attach(part1)

        return self

    def add_attachment(self, binary, show_name = None):
        """
        添加附件, 例如 excel等
        :param show_name:
        :param binary:
        :return:
        """

        if type(binary) is unicode:
            binary = binary.encode("utf-8")

        if type(show_name) is unicode:
            show_name = show_name.encode('utf-8')
        elif type(show_name) is str:
            pass
        else:
            show_name = '附件_%d' % self.attach_count

        attch1 = MIMEBase('application', 'octet-stream')
        attch1.set_payload(binary)
        # # Encode the payload using Base64
        encoders.encode_base64(attch1)
        attch1['Content-Disposition'] = 'attachment; filename=%s;' % Header(show_name, 'utf-8')
        self.msg.attach(attch1)
        self.attach_count += 1

        return self

    def add_picture(self, binary, c_type, show_name=None):

        if type(show_name) is unicode:
            show_name = show_name.encode('utf-8')
        elif type(show_name) is str:
            pass
        else:
            show_name = '图片_%d' % self.picture_count

        html = MIMEText('<img src="cid:%d" width="100%%"><br>' % self.picture_count, 'html')
        self.msg.attach(html)
        image = MIMEBase('image', c_type)
        # 加上必要的头信息:
        image['Content-Disposition'] = 'attachment; filename=%s;' % Header(show_name, 'utf-8')
        image.add_header('Content-ID', '<%d>' % self.picture_count)
        image.add_header('X-Attachment-Id', '0')
        # 把附件的内容读进来:
        image.set_payload(binary)
        # 用Base64编码:
        encoders.encode_base64(image)
        self.picture_count += 1
        self.msg.attach(image)
        return self

    def send(self):
        """
        发送邮件
        :return:
        """
        server = smtplib.SMTP()
        try:
            server.connect(mail_host)
            server.login(mail_user, mail_pass)
            server.sendmail(mail_user, self.mailto_list, self.msg.as_string())
        finally:
            server.close()
        return True


if __name__ == '__main__':

    mail = DefaultEmail(subject=u'测试邮件', mailto_list=['xxxxx@qq.com'])
    mail.add_content(u"测试邮件")

    mail.send()