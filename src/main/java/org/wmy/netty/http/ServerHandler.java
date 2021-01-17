package org.wmy.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @author wmy
 * @create 2020-12-13 17:29
 *
 * SimpleChannelInboundHandler 是ChannelInboundHandlerAdapter 的子类
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    /**
     * 读取客户端发送过来的数据
     * 注意：浏览器访问接口时，会发出两次请求，第一次时请求数据；第二次时请求图标资源
     * @param ctx
     * @param msg 客户端和服务端相互通讯的数据被封装成了HttpObject
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断数据是否是HttpRequest
        if (msg instanceof HttpRequest){

            //服务器会为每一个客户端创建一个 pipeLine 和 Handler，即客户端独享 pipeLine 和 Handler
            System.out.println("pipeLine hashCode："+ctx.pipeline().hashCode());
            System.out.println("handler hashCode："+this.hashCode());

            System.out.println("msg的类型是："+msg.getClass());
            System.out.println("数据来自："+ctx.channel().remoteAddress()+" 客户端");

            HttpRequest httpRequest = (HttpRequest) msg;
            System.out.println("httpRequest Uri："+httpRequest.uri());
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了 /favicon.ico，不做响应");
                return;
            }

            //通过http协议组装数据返回给客户端
            //1.构建输出内容缓存
            ByteBuf buf = Unpooled.copiedBuffer("Hello client",CharsetUtil.UTF_8);
            //2.构造http响应，即HttpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, buf);
            //3.设置响应头
            response.headers().add(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
            //4.输出响应
            ctx.writeAndFlush(response);

        }
    }
}
