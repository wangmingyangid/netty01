package org.wmy.buffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * 使用 ServerSocketChannel、socketChannel演示
 * @author wmy
 * @create 2020-12-06 20:25
 *
 * Scattering：将数据写入buffer时，可以采用buffer数组，依次写入
 * Gathering：从buffer读取数据时，可以采用buffer数组，依此读
 */
public class ScatteringAndGathering {
    public static void main(String[] args) throws IOException {

        //创建ServerSocketChannel
        ServerSocketChannel channel = ServerSocketChannel.open();
        //绑定端口到socket，并启动
        channel.socket().bind(new InetSocketAddress(7000));

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接
        SocketChannel socketChannel = channel.accept();

        //假定从客户端接受8个字节
        int messageLength = 8;

        while (true){
            int byteRead = 0 ;
            while (byteRead < messageLength){
                long read = socketChannel.read(byteBuffers);
                //累计读取到的字节数
                byteRead += read;
                System.out.println("byteRead = "+byteRead);

                //使用流打印，看看当前buffer数组，各个元素的状态
                Arrays.stream(byteBuffers)
                        .map(buffer->"position = "+buffer.position()+",limit = "+buffer.limit())
                        .forEach(System.out::println);

                //将所有的buffer进行反转
                Arrays.stream(byteBuffers).forEach(ByteBuffer::flip);
                //将数据读出显示到客户端
                long byteWrite = 0;
                while (byteWrite < messageLength){
                    long write = socketChannel.write(byteBuffers);
                    byteWrite += write;
                }
                //将所有的buffer进行clear
                Arrays.stream(byteBuffers).forEach(ByteBuffer::clear);

                System.out.println("byteRead = "+byteRead+",byteWrite = "+byteWrite);
            }
        }
    }
}
