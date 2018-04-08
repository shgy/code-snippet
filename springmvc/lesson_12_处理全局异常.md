使用`ControllerAdvice`注解
```
package com.springapp.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by shgy on 18-4-8.
 */

@ControllerAdvice
public class AControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleGlobalException(Exception ex){
        return  ClassUtils.getShortName(ex.getClass()) + ex.getMessage();
    }
}
```

