package com.shgy;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class EsSplit extends InputSplit implements Writable {

    private SplitLocationInfo info;

    public EsSplit(){}

    public EsSplit(String shard){
        info = new SplitLocationInfo(shard,true);
    }

    @Override
    public long getLength() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public String[] getLocations() throws IOException, InterruptedException {
        return new String[]{info.getLocation()};
    }

    @Override
    public SplitLocationInfo[] getLocationInfo() throws IOException {
        return new SplitLocationInfo[]{info};
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out,info.getLocation());

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        info = new SplitLocationInfo(Text.readString(in),true);
    }
}

class EsReader extends RecordReader<LongWritable, Text> {
    private int index=0;
    private String[] data = {"hello","elasticsearch"};

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {

    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return index < data.length;
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return new LongWritable(index);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(data[index++]);

    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return index/data.length;
    }

    @Override
    public void close() throws IOException {

    }
}

public class EsInputFormat extends FileInputFormat {

    private Logger LOG = LoggerFactory.getLogger(EsInputFormat.class);

    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException {
        LOG.info("call getSplits method...");
        List<InputSplit> splits = new ArrayList<>();
        for(int i=0; i<10; i++){
            splits.add(new EsSplit(""+i));
        }
        return splits;
    }

    @Override
    public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        LOG.info("call createRecordReader method...");
        return new EsReader();
    }
}
