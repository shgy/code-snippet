package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TransportBitCntAction extends HandledTransportAction<BitCntRequest, BitCntResponse> {
    private final ClusterService clusterService;

    private final TransportShardBitCntAction shardAction;

    @Inject
    public TransportBitCntAction(Settings settings, ThreadPool threadPool, TransportService transportService,
                                 ClusterService clusterService, TransportShardBitCntAction shardAction,
                                 ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver) {
        super(settings, BitCntAction.NAME, threadPool, transportService, actionFilters, indexNameExpressionResolver, BitCntRequest.class);
        this.clusterService = clusterService;
        this.shardAction = shardAction;
    }

    private class ExceptHolder{

        private Throwable e;
        private AtomicBoolean hasError = new AtomicBoolean(false);
        public ExceptHolder(){}

        public void setE(Throwable e){
            this.e = e;
            this.hasError.set(true);
        }

        public boolean hasError(){
            return hasError.get();
        }


    }

    @Override
    protected void doExecute(final BitCntRequest request, final ActionListener<BitCntResponse> listener) {
        ClusterState clusterState = clusterService.state();
        clusterState.blocks().globalBlockedRaiseException(ClusterBlockLevel.READ);

//        final AtomicArray<MultiGetItemResponse> responses = new AtomicArray<>(request.items.size());
        final Map<ShardId, BitCntShardRequest> shardRequests = new HashMap<>();

        String concreteSingleIndex = indexNameExpressionResolver.concreteSingleIndex(clusterState, request);

        IndexMetaData indexMeta = clusterState.getMetaData().index(concreteSingleIndex);
        int sectionCnt = indexMeta.getSettings().getAsInt("index.bitmap.section.num",-1);

        for(int i=0;i<sectionCnt;i++){

            ShardId shardId = clusterService.operationRouting()
                    .getShards(clusterState, concreteSingleIndex, request.getBitMeta().getType(), "", String.valueOf(i), null).shardId();
            BitCntShardRequest shardRequest = shardRequests.get(shardId);
            if (shardRequest == null) {
                shardRequest = new BitCntShardRequest(request, shardId.index().name(), shardId.id());
                shardRequests.put(shardId, shardRequest);
            }
            shardRequest.add(i);
        }

        final AtomicInteger counter = new AtomicInteger(shardRequests.size());

        final AtomicInteger value = new AtomicInteger(0);

        final ExceptHolder holder = new ExceptHolder();




        for (final BitCntShardRequest shardRequest : shardRequests.values()) {
            shardAction.execute(shardRequest, new ActionListener<BitCntShardResponse>() {
                @Override
                public void onResponse(BitCntShardResponse response) {
                    value.addAndGet(response.cnt());
                    if (counter.decrementAndGet() == 0) {
                        finishHim();
                    }
                }

                @Override
                public void onFailure(Throwable e) {

                    holder.setE(e);
                    if (counter.decrementAndGet() == 0) {
                        finishHim();
                    }
                }

                private void finishHim() {
                    if(!holder.hasError()){
                        listener.onResponse(new BitCntResponse(value.get()));
                    }else{
                        listener.onFailure(holder.e);
                    }

                }
            });
        }

    }
}
