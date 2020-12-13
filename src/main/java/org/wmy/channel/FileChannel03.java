package org.wmy.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * @author wmy
 * @create 2020-12-06 17:46
 */
public class FileChannel03  {
    public static void main(String[] args) throws Exception {
        //获得文件输入流和输出流
        FileInputStream inputStream = new FileInputStream("1.txt");
        FileOutputStream outputStream = new FileOutputStream("2.txt");

        //获得通道
        FileChannel inputChannel = inputStream.getChannel();
        FileChannel outputChannel = outputStream.getChannel();

        //创建缓存
        ByteBuffer buffer = ByteBuffer.allocate(512);

        //文件拷贝
        while (true){
            //重要，一定要复位，否则陷入死循环
            buffer.clear();
            //从通道读取数据并放入缓存区
            int read = inputChannel.read(buffer);
            if(read == -1){
                break;
            }
            buffer.flip();
            //把缓冲区的数据写入到通道
            outputChannel.write(buffer);
        }
        //关闭流
        inputStream.close();
        outputStream.close();
    }
}
