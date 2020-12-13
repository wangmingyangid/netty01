package org.wmy.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author wmy
 * @create 2020-12-12 10:46
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        /**
         * 创建两个 NioEventLoopGroup：bossGroup 和 workerGroup
         * bossGroup 只处理连接请求  真正和业务端处理会交给 workerGroup
         * 两个都是无限循环
         *
         * bossGroup和workerGroup 含有的子线程的个数（NioEventLoop），默认是
         * CPU核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        //异常发生时 finally 关闭服务端
        try {
            //创建服务端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();

            //链式编程配置启动对象的参数
            bootstrap.group(bossGroup,workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//服务端通道的实现
                    .option(ChannelOption.SO_BACKLOG,128)//TODO ？ 设置线程队列，得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)//TODO ？ 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        //给 pipeLine 设置处理器（channel 和 pipeLine 是你中有我我中有你的关系）
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//给workerGroup 的EventLoop 对应的管道设置处理器 （Handler）

            System.out.println("服务器已经准备好了....");
            //TODO ? 绑定端口，并且进行同步
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            //TODO ? 对关闭端口进行监听
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
