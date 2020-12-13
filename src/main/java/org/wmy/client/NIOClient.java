package org.wmy.client;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wmy
 * @create 2020-12-08 19:34
 */
public class NIOClient {
    public static void main(String[] args) throws Exception {
        //创建客户端channel
        SocketChannel socketChannel = SocketChannel.open();
        //设置为非阻塞
        socketChannel.configureBlocking(false);
        //绑定ip和端口
        if (!socketChannel.connect(new InetSocketAddress("127.0.0.1",6666))){
            while (!socketChannel.finishConnect()){
                System.out.println("连接服务端需要时间，等待的时候可以做其它工作...");
            }
        }
        //连接成功，发送数据
        String str = "hello,wmy";
        //创建缓存(wrap方法可以分配合适的缓存大小，不多不少)
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        //发送数据
        socketChannel.write(buffer);

        //把客户端阻塞在这里
        System.in.read();
    }
}
