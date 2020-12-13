package org.wmy.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wmy
 * @create 2020-12-09 13:49
 */
public class NIOChatServer {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private static final int port = 7000;

    private NIOChatServer(){
        try {
            //创建ServerSocketChannel
            this.serverSocketChannel = ServerSocketChannel.open();
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            //创建Selector
            selector = Selector.open();
            //注册通道到选择器
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * 监听事件
     */
    private void listen(){
        while (true){
            try {
                //阻塞式方法，没有事件发生就等待
                selector.select();
                //得到事件集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                //遍历事件集合，分情况处理
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    //连接事件
                    if(selectionKey.isAcceptable()){
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println(socketChannel.getRemoteAddress()+"上线了");

                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }
                    //可读事件
                    if (selectionKey.isReadable()){
                        //获得key对应的通道
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        //服务端打印输出
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);
                        String msg = new String(byteBuffer.array());

                        System.out.println("要转发的消息是："+msg);

                        //转发到其它客户端
                        sendToAnotherClient(selectionKey,msg);
                    }
                    //处理完事件，要移除
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服务器转发消息到其它客户端
     *
     * @param self 发消息的客户端
     */
    private void sendToAnotherClient(SelectionKey self,String msg) {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
             Channel channel = key.channel();
             if (channel instanceof SocketChannel && key!=self){
                 SocketChannel socketChannel = (SocketChannel) channel;
                 ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                 try {
                     socketChannel.write(buffer);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
    }


    public static void main(String[] args) {
        new NIOChatServer().listen();
    }
}
