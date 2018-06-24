
```
String[] classpathArray = config.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH, YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH);
String cp = "$PWD/*:" +  StringUtils.join(":", classpathArray);
config.set(YarnConfiguration.YARN_APPLICATION_CLASSPATH, cp);
```

参考: http://superlxw1234.iteye.com/blog/1766327



