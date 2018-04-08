
在mvc-dispatcher-servlet.xml文件中, 配置项很简单,只有两个.
第一个是为了支持注解功能.
```
    <context:component-scan base-package="com.springapp.mvc"/>
```
像是HelloController.java中的
```
@Controller
@RequestMapping("/")
```
这些注解都是因为配置了`component-scan`才得以实现的.

第二个是为了通过相关的url定位到对应的jsp文件, 即springmvc通过视图解析器来定位页面;
```
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/pages/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
```
比如HelloController.java中`printWelcome`返回了`hello`字符串, 那么jsp文件的所在地就是:`/WEB-INFO/pages/hell.jsp`文件.

springmvc支持链式的解析器. 即: 能解析的，不继续往下找，不能解析的，要继续往下找解析器. 解析器的先后顺序可以通过order参数指定.
```
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/pages/"/>
        <property name="suffix" value=".jsp"/>
        <property name="order" value="10"/>
    </bean>
```

除了`InternalResourceViewResolver`, springmvc中还有那些解析器, 都有那些功能呢?
1. `org.springframework.web.servlet.view.velocity.VelocityViewResolver`
   用于解析velocity模板.
2. `org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer`
   用于解析freemarker模板.
3. `org.springframework.web.servlet.view.json.MappingJacksonJsonView`
   用于返回json数据.
4. `org.springframework.web.servlet.view.xml.MarshallingView`
   用于解析`xml jaxb`视图
5. `org.springframework.web.servlet.view.xml.MarshallingView`
   用于解析`xml xstream`视图
6. `org.springframework.web.multipart.commons.CommonsMultipartResolver`
   文件上传功能

相关的配置如下:
```
<!-- jsp视图解析器 -->
<!-- <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver" p:prefix="/jsp/" p:suffix=".jsp" /> -->
<bean id="jspViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
    <property name="prefix" value="/jsp/"/>
    <property name="suffix" value=".jsp"/>
</bean>

<!-- velocity视图解析器 -->
<bean id="velocityViewResolver" class="org.springframework.web.servlet.view.velocity.VelocityViewResolver">
    <property name="cache" value="true"/>
    <property name="prefix" value="/velocity/"/>
    <property name="suffix" value=".vm"/>
</bean>

<!-- velocity环境配置 -->
<bean id="velocityConfig" class="org.springframework.web.servlet.view.velocity.VelocityConfigurer">
    <!-- velocity配置文件路径 -->
    <property name="configLocation" value="/WEB-INF/velocity.properties"/>
    <!-- velocity模板路径 -->
    <property name="resourceLoaderPath" value="/WEB-INF/velocity/"/>
</bean>

<!-- FreeMarker环境配置 -->
<bean id="freemarkerConfig" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
    <!-- freemarker模板位置 -->
    <property name="templateLoaderPath" value="/WEB-INF/freemarker/"/>
</bean>

<!-- FreeMarker视图解析 -->
<bean id="freeMarkerViewResolver" class="org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver">
    <property name="cache" value="true"/>
    <property name="prefix" value="/freemaker/"/>
    <property name="suffix" value=".ftl"/>
</bean>

<!-- Json视图解析 -->
<bean name="jsonView" class="org.springframework.web.servlet.view.json.MappingJacksonJsonView">
    <property name="encoding">
        <value type="org.codehaus.jackson.JsonEncoding">UTF8</value>
    </property>
    <property name="extractValueFromSingleKeyModel" value="true"></property>
    <property name="contentType" value="application/json;charset=UTF-8" />
</bean>

<!-- xml jaxb视图解析 -->
<bean id="xmlViewer" class="org.springframework.web.servlet.view.xml.MarshallingView">
    <constructor-arg>
      <bean class="org.springframework.oxm.jaxb.Jaxb2Marshaller">
        <property name="classesToBeBound">
            <list>
            </list>
        </property>
      </bean>
    </constructor-arg>
    <property name="contentType" value="application/xml;charset=UTF-8" />
</bean>

<!-- xml Xstream视图解析 -->
<bean id="xmlViewer" class="org.springframework.web.servlet.view.xml.MarshallingView">
    <property name="xStreamMarshaller">
        <bean class="org.springframework.oxm.xstream.XStreamMarshaller">
        <!-- 启用annotation -->
        <property name="autodetectAnnotations" value="true" />
        <!-- 支持在dto列表 -->
        <property name="supportedClasses"><array></array></property>
    </property>
</bean>

<!-- 文件上传 -->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <!--1024**1024*5即5M-->
    <property name="maxUploadSize" value="5242880"/>
</bean>
```

参考:
http://blog.csdn.net/mawming/article/details/52128472
https://www.ibm.com/developerworks/cn/java/j-lo-springview/
https://segmentfault.com/a/1190000011682174


