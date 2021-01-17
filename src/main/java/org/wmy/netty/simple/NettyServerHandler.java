package org.wmy.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author wmy
 * @create 2020-12-12 11:33
 *
 * 自定义Handler需要继承一个适配器，才能称之为Handler
 *
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * NIOEventLoop中的任务队列
     *
     * @param ctx 上下文对象，含有：pipeline管道、channel通道、地址
     * @param msg 客户端发送过来的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("执行普通任务的线程："+Thread.currentThread().getName()+"线程Id："+
                Thread.currentThread().getId());

        /**
         * 假设这里有一个耗时任务
         * 解决方案：耗时任务->异步执行->提交任务到该channel 对应的EventLoop 的taskQueue中
         */

        //方案1：用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("执行普通任务的线程："+Thread.currentThread().getName()+"线程Id："+
                            Thread.currentThread().getId());
                    //模拟耗时任务
                    Thread.sleep(5*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,client 2",
                            CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //通过debug 的方式可以看到ctx->pipeline->channel->eventLoop->taskQueue中此时有两个任务
        //taskQueue中只有一个线程，"Hello,client 3" 会在 "Hello,client 2" 执行完后的10s执行
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("执行普通任务的线程："+Thread.currentThread().getName()+"线程Id："+
                            Thread.currentThread().getId());
                    //模拟耗时任务
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,client 3",
                            CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //方案2：用户自定义延时任务
        //ctx->pipeline->channel->eventLoop->scheduledTaskQueue中此时有1个任务
        ctx.channel().eventLoop().schedule(()->{
            try {
                System.out.println("执行普通任务的线程："+Thread.currentThread().getName()+"线程Id："+
                        Thread.currentThread().getId());
                //模拟耗时任务
                Thread.sleep(5*1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,client 4",
                        CharsetUtil.UTF_8));
            }catch (Exception e){
                e.printStackTrace();
            }
        },5,TimeUnit.SECONDS);

        System.out.println("服务端 go on!");
    }

    /**
     * 读取数据，在这里可以读取客户端发送过来的数据
     *
     * @param ctx 上下文对象，含有：pipeline管道、channel通道、地址
     * @param msg 客户端发送过来的数据
     * @throws Exception
     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        System.out.println("服务器端读取数据的线程："+Thread.currentThread().getName());
//
//        /**
//         * 实验：通过debug的方式，了解ctx、pipeline、channel
//         * pipeline、channel是你中有我我中有你的关系
//         *
//         */
//        System.out.println("看看pipeline和channel的关系...");
//        ChannelPipeline pipeline = ctx.pipeline();
//        Channel channel = ctx.channel();
//
//        //将数据转为ByteBuf，注意不是 NIO 的 ByteBuffer
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("客户端发送过来的消息是："+buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端的地址是"+ channel.remoteAddress());
//
//    }




    /**
     * 读取数据完毕
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //TODO writeAndFlush 是两步操作 write + flush
        //将数据写入缓存并刷新
        //一般来说，需要对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,client 1",CharsetUtil.UTF_8));
    }

    /**
     * 异常发生时的处理方法:一般需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
