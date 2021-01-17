package org.wmy.netty_in_action;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author wmy
 * @create 2021-01-16 16:34
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group){
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            //握手成功后，移除 HttpRequestHandler ，因为将不会接受到任何 http 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            //通知所有已经连接的 WebSocket 客户端
            group.writeAndFlush(new TextWebSocketFrame("Client "+ctx.channel()+" jointed"));
            group.add(ctx.channel());
        }else {
            super.userEventTriggered(ctx, evt);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 写出到channelGroup中所有已经连接的客户端
        // 由于所有的操作都是异步的，因此，writeAndFlush()方法可能会在 channelRead0()方法返回之后完成，
        // 所以调用msg.retain()增加引用计数
        group.writeAndFlush(msg.retain());
    }
}
