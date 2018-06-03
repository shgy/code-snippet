#!/bin/bash
curl -XGET http://localhost:9200/company/_search?pretty=true -d '
{
  "size": 0,
  "aggs": {
    "stype": {
      "terms": {
        "field": "style_id"
      }
    }
  }
}'

