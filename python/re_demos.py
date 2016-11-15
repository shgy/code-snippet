__author__ = 'shgy'

import re

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
