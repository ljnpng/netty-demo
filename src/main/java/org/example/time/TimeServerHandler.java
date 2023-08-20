package org.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf buffer = ctx.alloc() // 获取一个ByteBuf的内存管理器
                .buffer(4); // 分配一个4字节的ByteBuf

        buffer.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        ChannelFuture channelFuture = ctx.writeAndFlush(buffer);
        channelFuture.addListener(f -> {
            assert channelFuture == f;
            ctx.close();
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
