package com.es.shgy.rest.action;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class HelloRestAction extends BaseRestHandler {

    @Inject
    public HelloRestAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(GET, "/hello/bitcnt", this);
    }
    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, "hello world"));
    }
}
