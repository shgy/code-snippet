package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class BitCntRequestBuilder extends ActionRequestBuilder<BitCntRequest, BitCntResponse, BitCntRequestBuilder> {

    public BitCntRequestBuilder(ElasticsearchClient client, BitCntAction action) {
        super(client, action, new BitCntRequest());
    }
}
