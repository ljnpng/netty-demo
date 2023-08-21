package org.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * TimeDecode
 * 自定义解码器
 * 从入站 ByteBuf 中提取即将要处理的帧
 * 从 ByteBuf 中读取 4 字节，将它们解码为一个 long，然后将其添加到解码消息的 List 中
 */
public class TimeDecode2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 从入站 ByteBuf 中提取即将要处理的帧
        out.add(in.readBytes(4));
    }
}
