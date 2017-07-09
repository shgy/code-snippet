mysql --local-infile -uroot -p

load data local infile '/home/shgy/hcb_work/autoemail/tests/email_list.csv'
into table auto_email_sender_list fields terminated by ','
optionally enclosed by '"'
lines terminated by '\n' (realname,emailbox,initialism);

head email_list.csv
"aliyun","aliyun@qq.com","aliyun"

