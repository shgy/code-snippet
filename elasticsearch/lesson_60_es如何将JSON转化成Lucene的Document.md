ES底层是基于Lucene实现全文检索功能的。ES添加索引时接收的参数是JSON, Lucene接收的参数是Document。这里必然存在一个转换的逻辑，将JSON转换成Lucene的Document对象。

将JSON转换成Document对象，必须清楚JSON中key对应的数据类型。

像MySQL这种数据库添加数据前必须有一个`create table`的操作， 该操作用于规范将要存储的数据的字段个数，每个字段的类型。基于这种模式存储的数据是一种结构化的数据。 ES中`put mapping`实际上跟`create table`是一样的。 只不过如果我们没有`put mapping`时，ES在内部自动帮我们做了这个操作而已。

了解了每个key对应的数据类型后， 我们解析JSON时，依据定义好的mapping，就可以将相应的字段转化成Field对象。先学习ES是如何解析JSON对象的。

```
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class XContentParseDemo {
    public static void main(String[] args) throws IOException {


        String json = "{\"hello\":{\"h1\": {\"h1\": \"world\"}},\"h2\":\"v1\"}";
        XContentParser parser = XContentType.JSON.xContent().createParser(json);
        XContentParser.Token token = parser.nextToken();
        while(token!=null){

            System.out.println(token);

            token = parser.nextToken();
        }
    }
}
```
即ES是一种采用深度优先遍历的方式来处理JSON。解析到每个字段，就基于字段名称，寻找该字段的mapping配置，进行相应的处理。例如：
```
1. BooleanFieldMapper

    protected void parseCreateField(ParseContext context, List<Field> fields) throws IOException {
        if (fieldType().indexOptions() == IndexOptions.NONE && !fieldType().stored() && !fieldType().hasDocValues()) {
            return;
        }

        Boolean value = context.parseExternalValue(Boolean.class);
        if (value == null) {
            XContentParser.Token token = context.parser().currentToken();
            if (token == XContentParser.Token.VALUE_NULL) {
                if (fieldType().nullValue() != null) {
                    value = fieldType().nullValue();
                }
            } else {
                value = context.parser().booleanValue();
            }
        }

        if (value == null) {
            return;
        }
        fields.add(new Field(fieldType().names().indexName(), value ? "T" : "F", fieldType()));
        if (fieldType().hasDocValues()) {
            fields.add(new SortedNumericDocValuesField(fieldType().names().indexName(), value ? 1 : 0));
        }
    }
```
通过上面的代码，可以知道，对于BooleanField， Lucene存储的是`T|F`字符串。

类似地，Binary, Byte, Date, Double,Float, Integer, Long, String, IP  都有相关的FieldMapper。 







