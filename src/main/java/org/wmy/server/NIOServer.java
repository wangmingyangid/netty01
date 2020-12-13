package org.wmy.server;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wmy
 * @create 2020-12-07 20:52
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {
        //获得ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //创建一个Selector对象
        Selector selector = Selector.open();
        //服务端绑定端口进行监听
        serverSocketChannel.bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //把ServerSocketChannel注册到Selector上，监听连接事件
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        //循环等待客户端连接
        while (true){
            //等待1秒，如果没有事件发生就返回
            if(selector.select(1000) == 0){
                System.out.println("服务器等待了1秒，没有连接");
                continue;
            }

            //有事件发生就获得 SelectionKey 的集合（该集合是选择器上有事件发生的SelectionKey，并非所有的SelectionKey）
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                //如果是 连接事件 发生
                if(selectionKey.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("有一个客户端连接了，socketChannel:"+socketChannel.hashCode());

                    //设置 socketChannel 为非阻塞
                    socketChannel.configureBlocking(false);
                    //将 socketChannel 注册到选择器，并分配buffer
                    socketChannel.register(selector,SelectionKey.OP_READ,
                            ByteBuffer.allocate(1024));
                }
                //如果是 读事件 发生
                if(selectionKey.isReadable()){
                    //通过 key 反向获取到对应的 channel
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    //获取到该 channel 中的buffer
                    ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println(new String(byteBuffer.array()));
                }
                //把处理后的事件移除
                iterator.remove();
            }


        }
    }
}
