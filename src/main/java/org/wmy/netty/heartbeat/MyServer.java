package org.wmy.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wmy
 * @create 2020-12-16 20:19
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
                            /**
                             * 1. IdleStateHandler 是netty提供的处理空闲状态的处理器
                             *      触发一个 IdleStateEvent 事件，当一个通道没有 读/写/读写 的行为
                             *      该事件会传递给 pipeline 的下个channel，通过调用该Handler的
                             *      userEventTriggered方法对事件进行处理
                             *
                             *
                             * 2. readerIdleTime 表示多长时间没有读，就会发送一个心跳检测包，检测是否连接
                             * 3. writerIdleTime 表示多长时间没有写，就会发送一个心跳检测包，检测是否连接
                             * 4. allIdleTime    表示多长时间没有读写，就会发送一个心跳检测包，检测是否连接
                             */
                            pipeline.addLast(new IdleStateHandler(3,5,
                                    7,TimeUnit.SECONDS));
                            //添加一个对空闲事件进行处理的处理器（自定义）
                            pipeline.addLast(new MyServerHandler());

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
