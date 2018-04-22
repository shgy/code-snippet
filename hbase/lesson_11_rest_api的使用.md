```

import requests, json, base64

resp = requests.get('http://localhost:8080/hbase_tab/00000fcd62cee9293f6192d54194f7eb/f1/', headers={"Accept":"application/json"})

data = dict(map(lambda x: (base64.decodestring(x["column"]).lstrip('f1:'), base64.decodestring(x["$"])), resp.json()['Row'][0]['Cell']))

print json.dumps(data, ensure_ascii=False, indent=True)

```


