package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.single.shard.TransportSingleShardAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.routing.ShardIterator;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class TransportShardBitCntAction  extends TransportSingleShardAction<BitCntShardRequest, BitCntShardResponse>{

    private static final String ACTION_NAME = BitCntAction.NAME + "[shard]";

    private final IndicesService indicesService;


    @Inject
    public TransportShardBitCntAction(Settings settings, ClusterService clusterService, TransportService transportService,
                                        IndicesService indicesService, ThreadPool threadPool, ActionFilters actionFilters,
                                        IndexNameExpressionResolver indexNameExpressionResolver) {
        super(settings, ACTION_NAME, threadPool, clusterService, transportService, actionFilters, indexNameExpressionResolver,
                BitCntShardRequest.class, ThreadPool.Names.GET);
        this.indicesService = indicesService;

    }


    @Override
    protected BitCntShardResponse shardOperation(BitCntShardRequest request, ShardId shardId) {
        int cnt = handleBitmap();
        return new BitCntShardResponse(cnt);
    }

    private int handleBitmap(){
        return 0;
    }

    @Override
    protected BitCntShardResponse newResponse() {
        return new BitCntShardResponse();
    }

    @Override
    protected boolean resolveIndex(BitCntShardRequest request) {
        return true;
    }

    @Override
    protected ShardIterator shards(ClusterState state, InternalRequest request) {
        return clusterService.operationRouting()
                .getShards(state, request.request().index(), request.request().shardId(), "");
    }

}
