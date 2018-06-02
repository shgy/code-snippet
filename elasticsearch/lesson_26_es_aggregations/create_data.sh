#!/bin/bash

curl -XDELETE 'http://localhost:9200/company/'

curl -XPUT 'http://localhost:9200/company/' -d '{
    "settings" : {
        "index" : {
            "number_of_shards" : 2,
            "number_of_replicas" : 1
        }
    }
}'

product_arr=("computer" "clothes" "food" "desk" "chair" "bed" "box" "bowl" "mirror" "laptop")

for i in {0..100}
do

curl -XPUT "localhost:9200/company/product/${i}?pretty" -d "
{  \"id\":${i},
  \"name\": \"${product_arr[i%10]}\",
  \"product_id\":$((i%10)),
  \"style_id\": $((i%5))
}"

done
