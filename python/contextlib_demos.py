__author__ = 'shgy'

from contextlib import contextmanager

# add html tag
@contextmanager
def tag(name):
    print '<%s>' % name
    yield
    print '</%s>' % name

with tag('h1'):
    print 'hello'

# test cpu speed
import time
@contextmanager
def time_print(task_name):
    t = time.time()
    try:
        yield
    finally:
        print task_name, "took", time.time() - t, "seconds."

with time_print("processes") as cpu_test:
    def hello():
        return [i for i in range(500)]
    hello()


@contextmanager
def closing(thing):
    try:
        yield thing
    finally:
        thing.close()

import urllib
with closing(urllib.urlopen('http://www.python.org')) as page:
    print page
    for line in page:
        print line
