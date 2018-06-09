Java API的变更主要体现在两个方面：
1. 标准化
```
commonTerms(...) renamed with commonTermsQuery(...)
queryString(...) renamed with queryStringQuery(...)
simpleQueryString(...) renamed with simpleQueryStringQuery(...)
textPhrase(...) removed
textPhrasePrefix(...) removed
textPhrasePrefixQuery(...) removed
filtered(...) removed. Use filteredQuery(...) instead.
inQuery(...) removed.
```

2. 第三方依赖内聚
```
com.google.common was org.elasticsearch.common
com.carrotsearch.hppc was org.elasticsearch.common.hppc
jsr166e was org.elasticsearch.common.util.concurrent.jsr166e
com.fasterxml.jackson was org.elasticsearch.common.jackson
org.joda.time was org.elasticsearch.common.joda.time
org.joda.convert was org.elasticsearch.common.joda.convert
org.jboss.netty was org.elasticsearch.common.netty
com.ning.compress was org.elasticsearch.common.compress
com.github.mustachejava was org.elasticsearch.common.mustache
com.tdunning.math.stats was org.elasticsearch.common.stats
org.apache.commons.lang was org.elasticsearch.common.lang
org.apache.commons.cli was org.elasticsearch.common.cli.commons
```

这样的话， 能尽量消除jar包依赖冲突带来的潜在不稳定性。

