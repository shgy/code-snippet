有个table， 希望根据选择指定单元格中含指定字符串的行。比如每行单元格2中不包含44的标红。
```
<!DOCTYPE html>
<html>
<head>
  <script type="text/javascript" src="/jquery/jquery.js"></script>
</head>

<body>
<table>
<tr>
<td>cell a 11</td>
<td>cell a 22</td></tr>
<tr>
<td>cell b 33</td>
<td>cell b 44</td></tr>
<tr>
<td>cell c 55</td>
<td>cell c 66</td></tr>
</table>
<script>
$("tr").each(function (){
   $(this).children('td:eq(1):not(:contains(4))').wrapInner("<font color=red></font>");
})
</script>
```

这个其实就相当与一个简易的筛选框了。简单但是实用。
