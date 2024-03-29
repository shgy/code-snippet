背景： 自动发邮件系统 中 填写用户的邮箱操作十分频繁， 而且收件人用的都是公司的邮箱。
因此， 做一个自动提示功能。
数据库表设计如下：
```
create table if not exists auto_email_sender_list
(
   id int(11) primary key auto_increment comment '主键',
   realname varchar(100) not null comment '邮箱账户姓名',
   emailbox varchar(100) not null comment '邮箱地址',
   initialism varchar(100) not null comment '首字母缩略词'

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发件人列表';
```

服务端取数据的接口如下：
```
def get_email_list(request):
    emails = AutoEmailSenderList.objects.values_list('realname', 'emailbox', 'initialism')
    data = {}
    for each in emails:
        data[''.join(each)] = each

    return HttpResponse(json.dumps(data,encoding='utf-8', ensure_ascii=False),
                        content_type="application/json; charset=UTF-8")
```

前端的JS代码如下：

难点： 一个输入框要输入多个收件人地址， 自动提示时。 不能受输入框已有收件人地址的影响。 这也是 `matcher`函数的功能所在。
```
        $.get('/email/email_list/', function (data) {
            var keys = [];
            for(var k in data) keys.push(k);
            var type_head = {
               source: keys,
               highlighter: function(item) {
                    return data[item][0]+'&lt;'+data[item][1]+'&gt;';
               },
               updater: function(item) {
                   var q = this.query;
                    var start = q.lastIndexOf(';')+1
                   var pre = q.substring(0,start)
                   return pre+data[item][1]+';';
               },
               matcher: function (item) {
                   var q = this.query;
                   var start = q.lastIndexOf(';')+1
                   var real_query = q.substring(start)
                   if (real_query.length <=0) return false;
                   return item.indexOf(real_query) >=0;
               }
           };

            $('#email_to').typeahead(type_head);
            $('#email_cc').typeahead(type_head);
            $('#email_dev').typeahead(type_head);
        });
```

input的html代码如下： 
```
<input class="form-control" id="email_to" name="email_to" type="text"
                        data-provide="typeahead" autocomplete="off"
                        value="{{ email_info.email_to }}"  placeholder="{{user.email}}" required/>
```

注意： autocomplete="off" 很重要。
