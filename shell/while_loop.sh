#!/bin/bash

var_i=2017

while [ $var_i -le `date +%Y` ]
do
   var_i=$(( $var_i + 1 ))
   echo $var_i

done
