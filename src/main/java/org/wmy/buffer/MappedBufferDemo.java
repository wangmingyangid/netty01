package org.wmy.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wmy
 * @create 2020-12-06 19:39
 */
public class MappedBufferDemo {
    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");

        //获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();
        /**
         * 参数1：使用的读写模式
         * 参数2：可以直接修改的起始位置
         * 参数3：把文件里的多少字节映射到内存（这里可以直接操作的索引时0到4）
         * MappedByteBuffer：实际类型时 DirectByteBuffer
         */
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        buffer.put(0,(byte) 'H');
        randomAccessFile.close();
        System.out.println("修改成功");
    }
}
