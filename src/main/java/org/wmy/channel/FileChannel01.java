package org.wmy.channel;


import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 向文件中写入数据
 * @author wmy
 * @create 2020-12-06 16:46
 */
public class FileChannel01 {
    public static void main(String[] args) throws Exception {
        //制造数据
        String str = "Hello,帅哥";
        //创建缓存
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //写入缓存
        buffer.put(str.getBytes());

        //为写入数据做准备
        buffer.flip();

        //创建通道
        FileOutputStream outputStream = new FileOutputStream("D:\\E\\workspace\\netty\\example.txt");
        //这个FileChannel的真实类型时FileChannelImpl
        FileChannel channel = outputStream.getChannel();
        //写入通道
        channel.write(buffer);
        //关闭
        outputStream.close();

    }
}
