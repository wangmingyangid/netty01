package org.wmy.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wmy
 * @create 2020-12-15 20:34
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个全局channel组，实现对所有channel 的管理（因为handler，每个客户端都有唯一的一个，所以用static，定义全局共享变量）
    //GlobalEventExecutor.INSTANCE 是全局事件执行器，是一个单例
    private static ChannelGroup channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 连接建立，第一个被执行的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        /**
         * 推送消息给其它在线的客户端
         * 该方法会自动变量 channelGroup 中的channel，不需要自己遍历
         */
        channelGroup.writeAndFlush(format.format(new Date())+":[客户端] "+channel.remoteAddress()+" 加入聊天");
        channelGroup.add(channel);

    }

    /**
     * 连接断开
     *  TODO 不需要channelGroup.remove() ，该方法的执行，会导致channelGroup.remove()方法自动执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(format.format(new Date())+":[客户端] "+channel.remoteAddress()+" 离开了");

        System.out.println("channelGroup size："+channelGroup.size());
    }

    /***
     * 表示通道处于活跃的状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(format.format(new Date())+":"+ctx.channel().remoteAddress()+"上线了");
    }

    /**
     * 表示通道处于不活跃的状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(format.format(new Date())+":"+ctx.channel().remoteAddress()+"离线了");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(ch->{
            if (ch != channel){//不是当前的channel，就转发消息
                ch.writeAndFlush("客户"+channel.remoteAddress()+"发送了："+msg+"\n");
            }else {//是当前的channel，就回显消息
                ch.writeAndFlush("自己发送了："+msg+"\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
