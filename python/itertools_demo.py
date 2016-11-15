__author__ = 'shgy'

from itertools import chain
for i in chain('ABC', 'EDF'):
    print i

# difference between izip and izip_longest
from itertools import izip
for i in izip([1, 2, 3], "AB"):
    print i

from itertools import izip_longest
for i in izip_longest([1,2,3], "AB", fillvalue='-'):
    print i

# count/cycle/repeat three Infinite Iterators
from itertools import islice, count
print [i for i in islice(count(), 5)]

from itertools import islice, cycle
print [i for i in islice(cycle('ABCDE'), 15)]

from itertools import repeat
print [i for i in repeat(10, 3)]

# difference between build-in map and itertools.imap
from itertools import imap, starmap
print [i for i in imap(lambda x, y: (x, y), [1, 2, 3], [4, 5])]
print map(lambda x, y: (x, y), [1, 2, 3], [4, 5])
print [i for i in starmap(pow, [(2, 5), (10, 3)])]

from itertools import takewhile, dropwhile
# takewhile return prefix   drop while return suffix
print [i for i in takewhile(lambda x:x > 5, [7, 9, 3, 5, 6])]
print [i for i in dropwhile(lambda x:x > 5, [7, 9, 3, 5, 6])]

from itertools import groupby
qs = [{'version': 1, 'date': 1}, {'version': 3, 'date': 2}, {'version': 1, 'date': 2}]
print [(name, list(group)) for name, group in groupby(qs, lambda p:p['date'])]

#
from itertools import ifilter, ifilterfalse
print [i for i in ifilter(lambda x: x > 5, [7, 9, 3, 5, 6])]
print [i for i in ifilterfalse(lambda x: x > 5, [7, 9, 3, 5, 6])]
print filter(lambda x: x > 5, [7, 9, 3, 5, 6])

from itertools import compress
print [i for i in compress('ABCDEF', [1,0,1,0,1,1])]

from itertools import product, combinations, combinations_with_replacement, permutations
print [i for i in product('AB', repeat=2)]
print [i for i in product('AB', 'xy')]

print [i for i in combinations('123', 2)]
print [i for i in combinations_with_replacement('123', 2)]
print [i for i in permutations('123', 2)]

