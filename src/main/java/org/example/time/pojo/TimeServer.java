package org.example.time.pojo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.time.TimeServerHandler;

public class TimeServer {
    private final int port;

    public TimeServer(int port) {
        this.port = port;
    }

    public void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup(1); // boss worker 模式
        NioEventLoopGroup workers = new NioEventLoopGroup(); // 默认线程数为 CPU 核心数 * 2

        try {
            ServerBootstrap b = new ServerBootstrap(); // 服务端启动引导类
            b.group(boss, workers) // 设置两个 EventLoopGroup 对象
                    .channel(NioServerSocketChannel.class) // 指定使用 NIO 的传输 Channel
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() { //ChannelInitializer 用于配置一个新的 Channel
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("encode", new TimeEncoder()) // outbound 和 inbound 互不影响
                                    .addLast("timeServerHandler", new TimeServerHandler());

                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync(); // 绑定端口，开始接收进来的连接
            f.channel().closeFuture().sync(); // 等待服务器 socket 关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 优雅的关闭
            workers.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = 8081;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new TimeServer(port).run();
    }

}
