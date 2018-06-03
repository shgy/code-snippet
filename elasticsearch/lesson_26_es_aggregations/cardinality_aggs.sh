#!/bin/bash
curl -XGET http://localhost:9200/company/_search?pretty=true -d '
{
  "size": 0,
  "aggs": {
    "distinct_style": {
      "filter": {
        "term": {
          "name": "computer"
        }
      },
      "aggs": {
        "distinct_style": {
          "cardinality": {
            "field": "style_id"
          }
        }
      }
    }
  }
}'

