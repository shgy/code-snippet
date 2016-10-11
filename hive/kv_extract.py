# -*- coding: utf-8 -*-
import sys
import string
reload(sys)
sys.setdefaultencoding('utf-8')

while True:
    line = sys.stdin.readline()
    if not line:
        break
    line = string.strip(line, "\n ")
    detail = line.split('\t')
    content = detail[0]
    try:
        content_detail = [kv.split('=') for kv in content.split(',')]
        for kv in content_detail:
            each_kv = detail + kv
            print '\t'.join(each_kv)
    except Exception, ex:
        pass