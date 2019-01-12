集成测试在es源码中的用法比较简单。我选择的切入口是delete-by-query插件。

将es插件的源码导入intellij后，直接运行类`DeleteByQueryRestIT`， 就会运行所有的集成测试用例。
```
-ea -Dtests.security.manager=false
```

这个运行结果，会让我产生两个疑问：

1. 这个类里面没有test注解的方法，其入口在哪里？
2. resource/rest-api-spec目录下，只有一个yaml文件，这些测试用例来自哪里？ 

通过查看其父类`ESRestTestCase`，可以看到里面有test注解的test方法，而且设置断点，可以确定该方法是测试的入口。

那么500多个测试用例来自哪里呢？

看`ESRestTestCase`类`@BeforeClass`注解的方法
```
    @BeforeClass
    public static void initExecutionContext() throws IOException, RestException {
        String[] specPaths = resolvePathsProperty(REST_TESTS_SPEC, DEFAULT_SPEC_PATH);
        RestSpec restSpec = null;
        FileSystem fileSystem = getFileSystem();
        // don't make a try-with, getFileSystem returns null
        // ... and you can't close() the default filesystem
        try {
            restSpec = RestSpec.parseFrom(fileSystem, DEFAULT_SPEC_PATH, specPaths);
        } finally {
            IOUtils.close(fileSystem);
        }
        validateSpec(restSpec);
        restTestExecutionContext = new RestTestExecutionContext(restSpec);
    }
```

通过了解这个方法的源码，可以了解到在`elasticsearch-2.4.5-tests.jar`里面，集成了这些测试用例。 
这些测试用例的源码就在`elasticsearch/rest-api-spec`目录下。

那么如何只运行插件中的测试用例而忽略jar包中的测试用例呢？
```
-Dtests.rest.load_packaged=false
```
直接运行，会发现测试用例运行失败。原来delete-by-query的测试用例有依赖。先编译源码:
编译前修改项目源码的pom.xml文件， 不能略过集成测试。
```
<skip.integ.tests>false</skip.integ.tests>
```

然后编译delete-by-query插件
```
elasticsearch/plugins/delete-by-query$ mvn install
```
编译完成后再运行DeleteByQueryRestIT类，即可成功运行插件的测试用例。
```
-ea -Dtests.security.manager=false -Dtests.rest.load_packaged=false
```

这里的测试用例会启用一个独立的es集群，用来运行测试用例，运行完成后再shutdown. 其功能由如下的脚本提供:
```
elasticsearch/dev-tools/src/main/resources/integration-tests.xml

```

参考:
http://david.pilato.fr/blog/2016/10/18/elasticsearch-real-integration-tests-updated-for-ga/


了解了集成测试插件的运行过程后，就可以在开发插件时自行实现集成测试的功能。而且通过yaml+json的方式，能够做到测试用例的复用。






