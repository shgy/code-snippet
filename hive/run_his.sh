#!/usr/bin/env bash

# usage: command file begin end

if [ $# != 3 ]; then
        echo "usage: run_his.sh sql_file start_date end_date" >&2
        exit 1
fi

file=$1
begin=$2
end=$3
nowdate=`date "+%Y-%m-%d"`
seq_begin=$(($(( $(date +%s -d$nowdate) - $(date +%s -d$begin)))/3600/24))
seq_end=$(($(( $(date +%s -d$nowdate) - $(date +%s -d$end)))/3600/24))

for i in `seq $seq_end $seq_begin`
do
  d=`date -d "-$i day" "+%Y-%m-%d"`
  echo $d
  sed -i.bak 's/${nowdate}/'$d'/g' "$file"
  hive -f ${file}  && \
  rm "$file" && \
  mv "$file".bak "$file"
done
