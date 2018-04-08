在开发中,前端传过来的参数不符合要求, 请求的资源没有权限...等情况是十分常见的. 如何处理呢?
以常用的用户模块为例:
UserController.java
```
package com.springapp.mvc;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by shgy on 18-4-8.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(){

        return "user/404";
    }

    @RequestMapping(value="/{id}/info", method = RequestMethod.GET)
    public String getUser(@PathVariable("id") final Integer id, final Model model){

        if(id < 0 ) throw new ResourceNotFoundException("");

        model.addAttribute("id", id);
        return "user/info";
    }

}

```
定义了一个方法, 根据id找到相应的用户, 当然这里没有实现相应的业务逻辑.  只是定义了一种非常简单的规则, 用户id不能为负数.

由于前端使用的是jsp, 所以定义了两个页面`404.jsp`和`info.jsp`. 目录结构如下:
```
├── src
│   ├── main
│   │   └── webapp
│   │       └── WEB-INF
│   │           ├── mvc-dispatcher-servlet.xml
│   │           ├── views
│   │           │   ├── index.jsp
│   │           │   └── user
│   │           │       ├── 404.jsp
│   │           │       └── info.jsp
│   │           └── web.xml
```
相关的内容很简单:
```
$ cat 404.jsp
<%--
  Created by IntelliJ IDEA.
  User: shgy
  Date: 18-4-8
  Time: 下午12:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>404</title>
</head>
<body>
   用户404
</body>
</html>

$ cat info.jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>SpringMVC 快速入门</title>
</head>
<body>

<h2>用户信息${ id }</h2>
</body>
</html>

```
由于使用了jstl标签, 因此, 需要引入mvn的包
```
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>taglibs</groupId>
            <artifactId>standard</artifactId>
            <version>1.1.2</version>
        </dependency>
```
这样就实现了自定义404页面.

如果是全局异常, 那么处理的方式有很多, 可以参考:
http://www.bubuko.com/infodetail-659904.html





