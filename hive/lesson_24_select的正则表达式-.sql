下面的查询，选择除了ds和hr的所有列。
SELECT `(ds|hr)?+.+` FROM sales ;

set hive.support.quoted.identifiers=None  (不支持中文)

https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Select#LanguageManualSelect-REGEXColumnSpecification

Whether to use quoted identifiers.  Value can be "none" or "column".

column:  Column names can contain any Unicode character. Any column name that is
specified within backticks (`) is treated literally. Within a backtick string,
use double backticks (``) to represent a backtick character.

none:  Only alphanumeric and underscore characters are valid in identifiers.
Backticked names are interpreted as regular expressions.
This is also the behavior in releases prior to 0.13.0.

