#!/bin/bash
# 计算当天距当月最后一天 的时间间隔 
if [ $(( `cal | sed -n '3,$p' | xargs | awk '{print $NF}'`  - `date +%e` )) -eq 1 ] 
then
  echo "equal"
fi
