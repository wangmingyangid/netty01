package org.wmy.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wmy
 * @create 2020-12-10 20:09
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {

        //创建服务端，并绑定端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(7000));

        //创建buffer
        ByteBuffer buffer = ByteBuffer.allocate(4096);

        while (true){
            System.out.println("等待连接...");
            //因为没有设置非阻塞模式，所以会阻塞
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("有连接进来了...");
            int read = 0;
            while (-1 != read){
                try {
                    int read1 = socketChannel.read(buffer);
                    read += read1;
                    // 把buffer倒带，方便下一次操作；position = 0;mark = -1
                    buffer.rewind();
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

            }
        }

    }
}
