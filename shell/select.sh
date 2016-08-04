#!/bin/bash
echo "What is your favourite color? "

select color in "red" "blue" "green" "white" "black"
do
 break
done

echo "You have selected $color"

