package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

public class BitCntShardResponse extends ActionResponse {

    private int cnt;

    public int cnt(){
        return cnt;
    }

    public BitCntShardResponse(){}

    public BitCntShardResponse(int cnt){
        this.cnt = cnt;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        cnt = in.readInt();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeInt(cnt);
    }
}
