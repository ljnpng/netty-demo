package org.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 抛弃任何进来的数据。
 * 这个服务器是一个抛弃任何进来的数据的服务器。
 * 它是一个无连接的协议，因此服务器只需要一个绑定的端口。
 * 要启动服务器，请在运行应用程序时将端口号指定为参数。
 * $ ./gradlew run -Pport=8080
 * 要测试服务器，请打开终端并运行以下命令：
 * $ nc localhost 8080
 * 您在终端中键入的任何内容都将发送到服务器，服务器将其丢弃。
 * 要停止服务器，请按Ctrl + C。
 */
public class DiscardServer {
    private final int port;

    public DiscardServer(int port) {
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
                            ch.pipeline().addLast("discardChannelHandler", new DiscardServerHandler()); // 添加一个 DiscardServerHandler 到子 Channel 的 ChannelPipeline
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
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }
}
