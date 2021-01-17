package org.wmy.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author wmy
 * @create 2020-12-17 19:39
 */
public class MyServer {
    public static void main(String[] args) {
        //创建两个事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //在bossGroup中增加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //因为是基于http协议，所以使用http协议的编解码器
                            pipeline.addLast(new HttpServerCodec());
                            //因为是以块方式写，所以添加 ChunkedWriteHandler 处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /**
                             * 1. http 数据在传输过程中是分段的；HttpObjectAggregator 可以将多个段聚合
                             * 2. 这也就是为什么，当浏览器发送大量数据时，就会发出多次 http 请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /**
                             * 1.对应webSocket；它的数据是以帧的形式传递
                             * 2.浏览器请求时 ws://localhost:7000/hello，表示请求的uri
                             * 3.WebSocketServerProtocolHandler 的核心功能是将http协议升级为ws协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                            //自定义handler，处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
