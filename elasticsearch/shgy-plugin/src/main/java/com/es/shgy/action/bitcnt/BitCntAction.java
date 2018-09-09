package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

public class BitCntAction extends Action<BitCntRequest, BitCntResponse, BitCntRequestBuilder> {

    public static final BitCntAction INSTANCE = new BitCntAction();
    public static final String NAME = "indices:data/read/bitcnt";

    private BitCntAction() {
        super(NAME);
    }

    @Override
    public BitCntResponse newResponse() {
        return new BitCntResponse();
    }

    @Override
    public BitCntRequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new BitCntRequestBuilder(client, this);
    }
}
