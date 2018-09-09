package com.es.shgy.action.bitcnt;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;

import java.io.IOException;

public class BitMeta implements Streamable {

    private String index;
    private String type;
    private String rule;
    private boolean flip;
    private boolean refresh;

    public BitMeta(){}

    public BitMeta(String index, String type, String rule) {
        this(index,type,rule, false,false);
    }

    public BitMeta(String index, String type, String rule, boolean flip, boolean refresh) {
        this.index = index;
        this.type = type;
        this.rule = rule;
        this.flip = flip;
        this.refresh = refresh;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public boolean getFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean getRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }


    @Override
    public void readFrom(StreamInput in) throws IOException {
        index = in.readString();
        type = in.readString();
        rule = in.readString();
        flip = in.readBoolean();
        refresh = in.readBoolean();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(index);
        out.writeString(type);
        out.writeString(rule);
        out.writeBoolean(flip);
        out.writeBoolean(refresh);
    }

    public boolean valid(){

        return index!=null && type !=null && rule!=null;
    }
}
