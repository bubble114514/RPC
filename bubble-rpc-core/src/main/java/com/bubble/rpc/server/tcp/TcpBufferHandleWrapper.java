package com.bubble.rpc.server.tcp;

import com.bubble.rpc.constant.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;



/**
 * 装饰者模式（使用recordParser对原有的buffer处理能力进行增强）
 */
public class TcpBufferHandleWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandleWrapper(Handler<Buffer> handler) {
        this.recordParser = initRecordParser(handler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> handler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            int size = -1;
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    // 读取消息头，获取body长度
                    size = buffer.getInt(13); // bodyLength在header的13字节位置
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 写入消息体并传递完整消息
                    resultBuffer.appendBuffer(buffer);
                    handler.handle(resultBuffer); // 关键修改：将完整消息传递给外部handler

                    // 重置状态
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
}