package org.wmy.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author wmy
 * @create 2020-12-06 18:04
 */
public class FileChannel04 {
    public static void main(String[] args) throws Exception {

        //获得文件输入流和输出流
        FileInputStream inputStream = new FileInputStream("1.txt");
        FileOutputStream outputStream = new FileOutputStream("2.txt");

        //获得通道
        FileChannel inputChannel = inputStream.getChannel();
        FileChannel outputChannel = outputStream.getChannel();

        outputChannel.transferFrom(inputChannel,0,inputChannel.size());

        //关闭流
        inputStream.close();
        outputStream.close();

    }

}
