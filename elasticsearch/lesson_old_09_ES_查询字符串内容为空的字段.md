方案1：使用正则查询

```

{

  "query": {

    "filtered": {

      "filter": {

        "not": {

          "regexp": {

            "field": ".+"

          }

        }

      }

    }

  }

}

或者：

{"query":{"bool":{"must_not":[{"regexp":{"field":".+"}}]}}}

```


