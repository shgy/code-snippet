#!/bin/bash
LIBDIR="${SYSROOT}/lib ${SYSROOT}/usr/lib ${CROSS_TOOL}/${TARGET}/lib"

find() {
   for d in $LIBDIR; do
      found=""
      if [ -f "${d}/$1" ]; then
         found="${d}/$1"
         break
      fi
   done
   
  if [ -n "$found" ]; then
     printf "%8s%s  => %s\n" "" $1 $found
  else
     printf "%8s%s ==> (not found)\n" "" $1 
  fi
}

readelf -d $1 | grep NEEDED \
| sed -r -e 's/.*Shared library:[ ]+\[(.*)\]/\1/;' \
| while read lib; do
     find $lib
  done
