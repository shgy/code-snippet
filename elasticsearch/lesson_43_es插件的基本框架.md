开发插件，能快速测试才能不断试错。
```
  <!-- elasticsearch and its test framework -->

      <dependency>
          <groupId>org.elasticsearch</groupId>
          <artifactId>elasticsearch</artifactId>
          <type>test-jar</type>
          <version>2.1.1</version>
          <scope>test</scope>
      </dependency>
      <dependency>
          <groupId>org.apache.lucene</groupId>
          <artifactId>lucene-test-framework</artifactId>
          <version>5.3.1</version>
          <scope>test</scope>
      </dependency>
```

然后就可以使用`ESSingleNodeTestCase`
