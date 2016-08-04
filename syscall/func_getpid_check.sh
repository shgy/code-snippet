#!/bin/bash
i=100000
while (( i > 0))
do
 ./a.out 
 let "i=i-1"
done
