package org.wmy.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * @author wmy
 * @create 2020-12-19 17:22
 *
 * TextWebSocketFrame:表示文本帧
 */
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息："+msg.text());
        //对收到的消息进行回显给客户端
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间"+LocalDateTime.now()+" "+msg.text()));
    }

    /**
     * 当客户端连接后会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id 表示唯一的值；longText 作为长文本输出，唯一；longText 作为短文本输出，可能不唯一；
        System.out.println("handlerAdded 被调用了"+ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用了"+ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved 方法被调用了"+ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生："+cause.getMessage());
        ctx.close();
    }
}
