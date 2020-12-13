package org.wmy.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 *
 * 从本地获取文件，进行网络传输（通过零拷贝的方式实现）
 * 实现零拷贝的方式：通过 transferTo 函数
 * 注意：在linux下使用一次 transferTo 函数就可以完成传输
 *      在Windows下调用一次 transferTo 只能传输 8M 的数据，因此文件超过 8M 就需要进行分段传输
 *
 * @author wmy
 * @create 2020-12-10 20:18
 */
public class NIOClient {

    private static final long EIGHT_M = 8388608;

    public static void main(String[] args) throws Exception {

        //创建socket 并绑定端口
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1",7000));

        //获得文件channel
        FileChannel channel = new FileInputStream("D:\\E\\学习记录\\网络通信\\netty框架.docx").getChannel();

        long size = channel.size();
        System.out.println("要传输的文件大小："+size);

        if (size <=EIGHT_M ){
            dealLow8M(channel,socketChannel);
        }else{
            dealHigh8M(channel,socketChannel);
        }

        //关闭
        channel.close();
    }

    private static void dealHigh8M(FileChannel channel,SocketChannel socketChannel) throws IOException {
        long start = System.currentTimeMillis();
        long size = channel.size();
        int count = (int)(size/EIGHT_M);
        int rest = (int)(size%EIGHT_M);
        int i =0;
        long transfer = 0;
        while (i < count){
            channel.transferTo(transfer,EIGHT_M,socketChannel);
            transfer += EIGHT_M;
            i++;
        }
        channel.transferTo(transfer,rest,socketChannel);
        long end = System.currentTimeMillis();
        System.out.println("传输文件的大小是："+size+"，耗时："+(end-start));
    }

    private static void dealLow8M(FileChannel channel,SocketChannel socketChannel) throws IOException {
        //准备发送
        long start = System.currentTimeMillis();
        long l = channel.transferTo(0, channel.size(), socketChannel);
        long end = System.currentTimeMillis();
        System.out.println("传输文件的大小是："+l+"，耗时："+(end-start));
    }
}
