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

for i in {1..100}
do
x=$RANDOM
echo "random=$x"
curl -XPUT "localhost:9200/company/product/${i}?pretty" -d "
{  \"id\":${i},
  \"name\": \"${product_arr[x%10]}\",
  \"product_id\":$((x%(i+x))),
  \"style_id\": $((x%i))
}"

done
