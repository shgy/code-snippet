#!/bin/bash
echo "param list is :""$@"
echo "param size is :""$#"
echo "first param is:""$1"

# check param size

if [ $# != 1 ] ; then
    echo "USAGE: $0 filename"
	exit 1;
fi
