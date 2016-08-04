package threadpooldemo;

import java.util.Iterator;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

class MultiThreadSearch extends Thread{
	
	private Client client;
	
	public MultiThreadSearch(Client client) {
		this.client = client;
	}
	
	public void run(){
		while(true){
			try {
				SearchResponse response = this.client.prepareSearch("dp_test")
						.setTypes("qyxx")
						.setQuery(
								QueryBuilders
								.boolQuery()
								.must(new QueryStringQueryBuilder("aaa").field("aaa"))
								)
						.setSize(1000)
						.execute()
						.actionGet();
			} catch (ElasticsearchException e) {
				System.out.println(e.getMessage());
			}
			
			
		}
	}
}

class MultiThreadIndex extends Thread{
	private Client client;
	
	public MultiThreadIndex(Client client) {
		this.client = client;
	}
	
//	private String gendoc(){
//		
//	}
	
	@Override
	public void run() {
		while(true){

			BulkRequestBuilder bulkRequest = client.prepareBulk();
				
			bulkRequest.add(client.prepareIndex("dp_test", "qyxx","aaaaaaaaaaaaaaaaaaaaaaaaaaa").setSource("{\"aaa\":\"this is sample test doc\"}"));
			bulkRequest.add(client.prepareIndex("dp_test", "qyxx").setSource("{\"aaa:\"this is sample test doc\"}"));
			
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				Iterator<BulkItemResponse> items = bulkResponse.iterator();
				while (items.hasNext()) {
					BulkItemResponse bulkItemResponse = (BulkItemResponse) items.next();
					if(bulkItemResponse.isFailed()){
						String failureMsg = bulkItemResponse.getFailureMessage();
						System.out.println(bulkItemResponse.getResponse());
						System.out.println(bulkItemResponse.getId()+": "+failureMsg);
					}
					
					
				}
			}
			
		}
	}
}
public class Main{	
	public static void main(String[] args) {
		
		Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress(
                        "localhost", 9300));
		
		for(int i=0;i<100;i++){
			new MultiThreadSearch(client).start();
		}
		
		
		
	}
	
	
}
