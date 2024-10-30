package org.TestSink;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import java.io.IOException;

public class TestToMysql implements Sink {
    @Override
    public SinkWriter createWriter(InitContext context) throws IOException {
        return null;
    }

    @Override
    public SinkWriter createWriter(WriterInitContext context) throws IOException {
        return Sink.super.createWriter(context);
    }
}
