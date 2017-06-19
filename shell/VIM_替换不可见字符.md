有时候会在文本中写入不可见字符来区分段落。 比如\0 这种。
如果想在vim中替换， 怎么办呢？
```
:set dy=uhex

You can try setting the encoding type and see if it fixes the visalizations of those characters:

:set encoding=utf-8
then you can use them directly. Alternatively, you can place your cursor on the unprintable character and hit ga, it will show the decimal/hex/octal code of that character, then you can substitute it with:

:%s/\%xYY/substitute/g
where YY is the hex code of the char, if it's multibyte:

:%s/\%uYYYY/substitute/g
for details:

:help character-classes
```
比如， 替换\0

```
echo -e "aa\0b" > tmp/aa.txt

:%s/\%x0/\t/g
```
