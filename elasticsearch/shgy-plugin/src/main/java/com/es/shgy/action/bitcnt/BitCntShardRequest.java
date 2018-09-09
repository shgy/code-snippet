package com.es.shgy.action.bitcnt;

import com.carrotsearch.hppc.IntArrayList;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.single.shard.SingleShardRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

public class BitCntShardRequest extends SingleShardRequest<BitCntShardRequest> {

    private int shardId;
    private BitMeta bitMeta;
    private IntArrayList sections;

    public BitCntShardRequest() {

    }

    BitCntShardRequest(BitCntRequest bitCntRequest, String index, int shardId) {
        super(bitCntRequest, index);
        this.shardId = shardId;
        this.bitMeta = bitCntRequest.getBitMeta();
        sections = new IntArrayList();
    }


    public int shardId(){
        return shardId;
    }

    @Override
    public ActionRequestValidationException validate() {

        return super.validateNonNullIndex();
    }

    public void add(int section){
        sections.add(section);
    }


    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        bitMeta = new BitMeta();
        bitMeta.readFrom(in);

        int cnt = in.readInt();
        sections = new IntArrayList(cnt);
        for(int i=0;i<cnt;i++){
            sections.add(i);
        }

    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        bitMeta.writeTo(out);
        out.writeInt(sections.size());
        for(int i=0;i<sections.size();i++){
            out.writeInt(sections.get(i));
        }
    }
}
