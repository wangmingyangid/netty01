package org.wmy.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author wmy
 * @create 2020-12-16 19:16
 */
public class NettyChatClient {
    private final String ip;
    private final int port;

    public NettyChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run(){
        EventLoopGroup loopGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //添加解码器
                            ch.pipeline().addLast("decoder",new StringDecoder());
                            //添加编码器
                            ch.pipeline().addLast("encoder",new StringEncoder());
                            //添加自定义的handler
                            ch.pipeline().addLast("myHandler",new GroupChatClientHandler());
                        }
                    });
            System.out.println("netty 客户端启动");
            ChannelFuture channelFuture = bootstrap.connect(ip,port).sync();

            Channel channel = channelFuture.channel();

            //TODO 加上后发送给服务器的消息未被转发 channel.closeFuture().sync();

            //客户端输入信息，创建一个键盘录入
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()){
                String msg = scanner.next();
                channel.writeAndFlush(msg+"\r\n");

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new NettyChatClient("127.0.0.1",7000).run();
    }
}
