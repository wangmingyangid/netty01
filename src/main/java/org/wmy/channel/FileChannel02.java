package org.wmy.channel;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 从文件中读出数据；假设文件已经存在
 * @author wmy
 * @create 2020-12-06 17:13
 */
public class FileChannel02 {
    public static void main(String[] args) throws Exception {
        //获得文件对象
        File file = new File("D:\\E\\workspace\\netty\\example.txt");
        //获得文件输入流
        FileInputStream inputStream = new FileInputStream(file);
        //获得channel
        FileChannel channel = inputStream.getChannel();
        //创建缓存
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
        //将通道的数据读入到缓存
        channel.read(buffer);
        //输出到控制台
        System.out.println(new String(buffer.array()));
    }
}
