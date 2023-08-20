package org.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理服务端的channel
 * ChannelInboundHandlerAdapter实现了ChannelInboundHandler，提供了各种事件处理方法，可以重写
 * 目前，只需要扩展ChannelInboundHandlerAdapter，而不是自己实现handler接口
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg; // ByteBuf是一个引用计数对象，必须通过release()方法显式释放
        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte()); // 读取一个字节
                System.out.flush(); // 将缓冲区的数据强制输出
            }
        } finally {
            // 这里需要手动释放msg
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常时，关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
