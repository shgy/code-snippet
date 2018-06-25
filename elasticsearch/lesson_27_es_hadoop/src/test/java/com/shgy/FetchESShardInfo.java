package com.shgy;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FetchESShardInfo {

    public static void main(String[] args) throws UnknownHostException {
        //测试获取es的分片数量
        // on startup

        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        GetIndexResponse resp = client.admin().indices().getIndex(new GetIndexRequest().indices("company")).actionGet();
        Settings settings = resp.getSettings().get("company");
        System.out.println(settings.getAsMap());
        System.out.println("index.number_of_shards="+settings.get("index.number_of_shards"));
// on shutdown

        client.close();
    }
}
