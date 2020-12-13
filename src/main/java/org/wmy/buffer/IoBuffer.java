package org.wmy.buffer;

import java.nio.IntBuffer;

/**
 * @author wmy
 * @create 2020-12-06 15:43
 */
public class IoBuffer {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(5);
        for (int i=0;i<buffer.capacity();i++){
            buffer.put(i*2);
        }
        //读写转换
        buffer.flip();
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }

}
