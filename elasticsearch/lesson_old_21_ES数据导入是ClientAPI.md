ES数据导入之Client API

Client API支持批量导入数据，可以配置两种网络连接的方式：TCP和UDP。一个简单的Demo即可了解Client的使用方法。

```

import org.elasticsearch.action.bulk.BulkItemResponse;

import org.elasticsearch.action.bulk.BulkRequest;

import org.elasticsearch.action.bulk.BulkResponse;

import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.client.Client;

import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.settings.ImmutableSettings;

import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.common.transport.InetSocketTransportAddress;



public class ClientDemo {

	public static void main(String[] args) {

		

		Settings settings = ImmutableSettings.settingsBuilder()

.put("cluster.name", "elasticsearch").build();

		

		try(

				Client client = new TransportClient(settings).addTransportAddress(

new InetSocketTransportAddress("localhost", 9300));) {

			

				BulkRequest request = new BulkRequest();

				request.add(new IndexRequest("company","location","24")

.source("sss","ff"));

				request.add(new IndexRequest("company","location","25")

.source("sss","ee"));

				

				BulkResponse response = client.bulk(request).actionGet();

				BulkItemResponse[] resps = response.getItems();

				

				for (BulkItemResponse bulkItemResponse : resps) {

					System.out.println(bulkItemResponse.isFailed());

				}

				client.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}

```


