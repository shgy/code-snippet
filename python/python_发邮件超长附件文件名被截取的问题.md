使用python发邮件， 文件的附件名如果比较长， 在浏览器中显现正常。 但是使用fireforx客户端发邮件， 文件名却被截取了。
如图figure-01.png

使用 firefox发送超长文件名的附件， 又显示正常。
 
问题定位： 浏览器显示正常， firefox客户端不正常， 一定是内容的解析出了问题。



处理方法： 使用wireshark抓包。 过滤器 imap
使用firfore发邮件，邮件的内容：
```
Content-Transfer-Encoding: base64
Content-Disposition: attachment;
	filename="=?GB2312?B?1tDOxMP7s8bW0M7Ew/uzxtbQzsTD+7PG1tDOxMP7s8bW0M7Ew/uzxtbQzsTD+7PGLnR4dA==?="
```
发现了双引号。 
将代码：
```
 attch1 = MIMEApplication(binary)
        attch1['Content-Disposition'] = 'attachment; filename=%s;' % Header(show_name, 'utf-8')
```
修改为
```
 attch1 = MIMEApplication(binary)
        attch1['Content-Disposition'] = 'attachment; filename="%s";' % Header(show_name, 'utf-8')
```

使用python发邮件的内容
```
Content-Disposition: attachment;
 filename="=?utf-8?b?5Lit5paH5ZCN56ew5Lit5paH5ZCN56ew5Lit5paH5ZCN56ew5Lit5paH5ZCN?=
 =?utf-8?b?56ew5Lit5paH5ZCN56ew5Lit5paH5ZCN56ewLnR4dA==?=";
\r\n
aGVsbG8gd29ybGQ=
```

在firefox中收取邮件， 正常。如图figure-02.png


