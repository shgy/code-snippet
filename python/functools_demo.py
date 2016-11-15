# -*- coding: utf-8 -*-
# Created by 'shgy' on '15-8-22'

"""
感觉partial与c语言的 typedef 类似
"""
from functools import partial
int2 = partial(int, base=2)

a = int2('100')
print a

from functools import cmp_to_key
"""
https://docs.python.org/2/library/functions.html#sorted
In general, the key and reverse conversion processes
are much faster than specifying an equivalent cmp function.
This is because cmp is called multiple times for each list
element while key and reverse touch each element only once.
Use functools.cmp_to_key() to convert an old-style cmp function to a key function.
"""
print sorted([1, 2, 3, 42, 1, 4], key=cmp_to_key(lambda x, y: x - y))

from functools import total_ordering
"""
The class must define one of __lt__(), __le__(), __gt__(), or __ge__().
In addition, the class should supply an __eq__() method.
自动补全其他的比较函数
"""


@total_ordering
class Person:

    def __init__(self, name, age):
        self.name = name
        self.age = age

    def __eq__(self, other):
        nm_cmp = cmp(self.name, other.name)
        if nm_cmp != 0:
            return cmp(self.age, other.age)
        return nm_cmp

    def __le__(self, other):
        nm_cmp = cmp(self.name, other.name)
        if nm_cmp < 0:
            return True
        elif nm_cmp > 0:
            return False
        age_cmp = cmp(self.age, other.age)

        if age_cmp < 0:
            return True

        return False

print dir(Person('tom', 2))
print Person('tom', 2) < Person('tom', 3)
