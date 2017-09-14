1. get_json_object()  适用于只取一个key
```
select a.timestamp, get_json_object(a.appevents, '$.eventid'), get_json_object(a.appenvets, '$.eventname') from log a;
```

2. json_tuple() 适用于取多个key
```
select a.timestamp, b.*
from log a lateral view json_tuple(a.appevent, 'eventid', 'eventname') b as f1, f2;
```
如果有nested, 
```
{
    "Foo": "ABC",
    "Bar": "20090101100000",
    "Quux": {
        "QuuxId": 1234,
        "QuuxName": "Sam"
    }
}

select v1.foo, v1.bar, v2.qid, v2.qname 
from json_table jt
     LATERAL VIEW json_tuple(jt.json, 'Foo', 'Bar', 'Quux') v1
     as foo, bar, quux
     LATERAL VIEW json_tuple(v1.quux, 'QuuxId', 'QuuxName') v2
     as qid, qname;
```
