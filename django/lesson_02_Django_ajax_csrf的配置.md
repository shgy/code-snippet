这个需要用户jquery.cookie.js包
```
    <script src="/static/js/jquery.cookie.js"></script>

```
然后在JavaScript中添加如下的代码
```
 $().ready(function() {

                $.ajaxSetup({
                    beforeSend: function(xhr, settings) {
                       xhr.setRequestHeader("X-CSRFToken", $.cookie("csrftoken"));
                    }
                });
 });
```
