package com.es.shgy.action.bitcnt;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.ValidateActions;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

public class BitCntRequest extends ActionRequest<BitCntRequest> implements IndicesRequest {

    public static final IndicesOptions INDICES_OPTIONS = IndicesOptions.strictSingleIndexNoExpandForbidClosed();

    private BitMeta bitMeta;

    public BitCntRequest(){}

    public BitCntRequest(String index, String type, String rule){
        bitMeta = new BitMeta(index, type, rule);
    }
    public BitCntRequest(String index, String type, String rule, boolean flip, boolean refresh){
        bitMeta = new BitMeta(index, type, rule,flip,refresh);
    }

    public BitMeta getBitMeta(){
        return bitMeta;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (!bitMeta.valid()) {
            validationException = ValidateActions.addValidationError("index is valid", validationException);
        }

        return validationException;
    }


    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        bitMeta = new BitMeta();
        bitMeta.readFrom(in);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        bitMeta.writeTo(out);

    }

    @Override
    public String[] indices() {
        return new String[]{bitMeta.getIndex()};
    }

    @Override
    public IndicesOptions indicesOptions() {
        return INDICES_OPTIONS;
    }
}
