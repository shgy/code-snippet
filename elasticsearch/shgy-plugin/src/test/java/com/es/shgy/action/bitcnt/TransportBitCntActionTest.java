package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

import java.io.IOException;

public class TransportBitCntActionTest extends ESSingleNodeTestCase {
    private XContentBuilder createMappingSource(String fieldType) throws IOException {
        return XContentFactory.jsonBuilder().startObject().startObject("my-type")
            .startObject("properties")
            .startObject("a")
            .field("type", fieldType)
            .startObject("fields")
            .startObject("b")
            .field("type", "string")
            .field("index", "not_analyzed")
            .endObject()
            .endObject()
            .endObject()
            .endObject()
            .endObject().endObject();
    }
    @Test
    public void testAction() throws IOException {

        client().admin().indices().prepareCreate("my-index")
            .addMapping("my-type", createMappingSource("geo_point"));

        client().prepareIndex("test", "type").setSource("num", 1).get();

        TransportBitCntAction action = getInstanceFromNode(TransportBitCntAction.class);
        BitCntRequest req = new BitCntRequest("bitmap30","bitmap_tag_imei","19");

        action.doExecute(req, new ActionListener<BitCntResponse>() {
            @Override
            public void onResponse(BitCntResponse bitCntResponse) {
                System.out.println(bitCntResponse.getCnt());
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });

    }
}
