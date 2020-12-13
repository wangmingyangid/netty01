package org.wmy.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author wmy
 * @create 2020-12-13 17:31
 *
 * 初始化通道的时候，往通道关联的管道内加入处理器
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获得管道
        ChannelPipeline pipeline = ch.pipeline();
        //HttpServerCodec 是netty 提供的处理http 的编解码器
        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        //添加自定义的handler
        pipeline.addLast("myHandler",new ServerHandler());

    }
}
