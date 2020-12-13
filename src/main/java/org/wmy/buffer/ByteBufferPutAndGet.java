package org.wmy.buffer;

import java.nio.ByteBuffer;

/**
 * put与get要对应，否则可能报异常
 * @author wmy
 * @create 2020-12-06 18:21
 */
public class ByteBufferPutAndGet {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(100);

        buffer.putInt(1);
        buffer.putLong(200L);
        buffer.putChar('e');

        buffer.flip();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getChar());

    }
}
