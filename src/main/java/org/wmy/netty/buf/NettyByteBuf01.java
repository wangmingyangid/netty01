package org.wmy.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;

/**
 * @author wmy
 * @create 2020-12-15 19:28
 */
public class NettyByteBuf01 {
    public static void main(String[] args) {
        /**
         *  1.创建缓存对象，该对象包含了一个数组byte[10]
         *  2.在netty的buf 中读写转换不需要使用flip方法，因为它底层维护了一个readerIndex 和 writerIndex(初始时都是0)
         *  3.通过readerIndex、writerIndex、capacity，将buf 分成了3个区域
         *      0-readerIndex：已经读取了的区域
         *      readerIndex-writerIndex：可以读取的区域
         *      writerIndex-capacity：可写的区域
         */
        ByteBuf buffer = Unpooled.buffer(10);
        //boolean release = buffer.release();
        //向缓存中写入数据
        for (int i = 0;i< 10; i++){
            buffer.writeByte(i);
        }

        ByteBuf buf = buffer.readSlice(3);
        buf.setByte(0,1);
        System.out.println(buf.getByte(0) == buffer.getByte(0));

//        ByteBuf buf = buffer.readBytes(3);
//        buf.setByte(0,1);
//        System.out.println(buf.getByte(0) == buffer.getByte(0));

//        ByteBuf buf1 = buffer.readSlice(3);
//        ByteBuf buf2 = buffer.readSlice(3);
//        ByteBuf buf3 = buffer.readSlice(3);

        System.out.println("buffer的容量是："+buffer.capacity());

        //读取缓存中的数据
        for (int i=0;i<5;i++){
            //该方法，readerIndex会自动增加
            System.out.println(buffer.readByte());
        }
        int i = buffer.maxCapacity();
        //buffer.discardReadBytes();

       // ByteBuf copy = buffer.copy();
       // boolean equals = ByteBufUtil.equals(copy, buffer);
        //ResourceLeakDetector.setLevel();
        //ReferenceCountUtil.release()
    }
}

