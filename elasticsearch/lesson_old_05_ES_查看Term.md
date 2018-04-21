在es中，希望查看分词的结果对不对，就可以使用如下的功能。

```

{

    "facets": {

        "my_terms": {

            "terms": {

                "size": 50,

                "field": "名称"

            }

        }

    }

}

```
