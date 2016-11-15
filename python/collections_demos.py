# -*- coding: utf-8 -*-
# Created by 'shgy' on '15-8-22'
from collections import Counter
c_chars = Counter('11aazz')
print c_chars
# output:   Counter({'1': 2, 'a': 2, 'z': 2})

c_words = Counter(['hello','world','hello'])
print c_words
# output:   Counter({'hello': 2, 'world': 1})

import re
words = re.findall(r'\w+', open('test-data/hamlet.txt').read().lower())
print Counter(words).most_common(10)    # find top 10
print Counter(words).most_common()[:-10-1:-1]       # 10 least common elements

"""
Return an iterator over elements repeating each as many times as its count.
Elements are returned in arbitrary order.
If an element’s count is less than one, elements() will ignore it.
"""
print list(Counter(words).elements())

# remove zero and negative counts
c = Counter(words)
c.update({"ab", 0})
c += Counter()
print c