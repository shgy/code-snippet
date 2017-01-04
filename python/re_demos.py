__author__ = 'shgy'

import re

# 匹配年月日
#  re.search method
g = re.search(r'(?P<year>\d{4})-(?P<month>\d{2})-(?P<day>\d{2})','2014-12-11')
print g.group('year')
print g.group('month')
print g.group('day')

# can make day empty
g = re.search(r'(?P<day>)/(?P<month>\d{2})/(?P<year>\d{4})','this is 11/12/2014')
print g.group('year')
print g.group('month')
print g.group('day')

# 匹配 省市 自治区
# 这里考察的是 python 正则表达式的应用,
# 在python中, 通常() 表示分组, 所以使用 (?:) 表示 正则表达式中原始的使用方式
# 这也是 为什么通常正则表达式 在python中的运行结果与预想不一样的原因
# 例如: (f|m)ood 匹配 food 和 mood
re.findall(ur'.+?(?:省|市|壮族自治区|回族自治区|维吾尔自治区|自治区|特别行政区)',u'四川省 广西壮族自治区 西藏自治区')

