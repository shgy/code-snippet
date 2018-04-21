业务描述：全国有许多法院，每个法院有很多法官，每个法官会审理很多案件。先按照法院聚合，然后按照法官聚合。

```

"aggs": {

    "group_court": {

      "terms": {

        "field": "court"

      },

      "aggs": {

        "group_jugde": {

          "terms": {

            "field": "judge",

            "size": 10

          }

        }

      }

    }

  }

```


