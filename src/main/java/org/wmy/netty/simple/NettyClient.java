package org.wmy.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wmy
 * @create 2020-12-12 22:15
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //客户端需要一个事件循环组
        EventLoopGroup executors = new NioEventLoopGroup();

        ////异常发生时 finally 关闭客户端
        try {
            //创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();
            //启动对象配置参数
            bootstrap.group(executors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("客户端准备好了...");
            //连接服务器
            //TODO channelFuture，涉及netty的异步机制
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }finally {
            executors.shutdownGracefully();
        }

    }
}
