使用Java API连接Hbase，需要注意的是hostname所配置的IP值。 假如机器的IP值为192.168.1.10，机器的hostname为hbase-dev,那么需要在/etc/hosts中配置：

```

127.0.0.1 localhost

192.168.1.10 hbase-dev

```

相关的代码如下：

```java

package bbd.hbase.raw;



import java.io.IOException;



import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.Cell;

import org.apache.hadoop.hbase.HBaseConfiguration;

import org.apache.hadoop.hbase.KeyValue;

import org.apache.hadoop.hbase.TableName;

import org.apache.hadoop.hbase.client.Admin;

import org.apache.hadoop.hbase.client.Connection;

import org.apache.hadoop.hbase.client.ConnectionFactory;

import org.apache.hadoop.hbase.client.Get;

import org.apache.hadoop.hbase.client.Result;

import org.apache.hadoop.hbase.client.Table;

import org.apache.hadoop.hbase.util.Bytes;



public class HbaseCrud {

	public static void main(String[] args) throws IOException {

		Configuration conf = HBaseConfiguration.create();

		conf.set("hbase.zookeeper.quorum", "fubin-laptop");

        Connection connection = ConnectionFactory.createConnection(conf);

        Admin admin = connection.getAdmin();

        

        Table table = connection.getTable(TableName.valueOf("table"));

        String rowKey="rowkey";

        Get get = new Get(rowKey.getBytes());

        Result rs = table.get(get);

        Cell cell = rs.getColumnLatestCell(Bytes.toBytes("f1"),Bytes.toBytes("名称"));

        String cellval = new String(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());

        System.out.println("========================================================"+cellval+"============================================================");

//        for(Cell cell :rs.listCells()){

////        	CellScanner cScan;

//       

//            System.out.println(new String(cell.getRowArray(),cell.getRowOffset(),cell.getRowLength()) + " " );

//            System.out.println(new String(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength()) + ":" );

//            System.out.println(new String(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength()) + " " );

//            System.out.println(new String(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength()));

//        }

//        for(KeyValue kv :rs.raw()){

//            String key = new String(kv.getQualifier());

//            String value = new String(kv.getValue());

//            System.out.println(key +"= "+Bytes.toString(value.getBytes()));

//        }

        System.out.println("====================================================================================================================");

        connection.close();

	}

}

```
