package org.wmy.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * @author wmy
 * @create 2020-12-15 19:58
 */
public class NettyByteBuf02 {
    public static void main(String[] args) {
        //创建缓存
        ByteBuf buf = Unpooled.copiedBuffer("hello,wmy", CharsetUtil.UTF_8);

        //相关方法的使用
        if (buf.hasArray()){//true 因为上面的操作会在buf 内部产生数组
            byte[] content = buf.array();
            //将content 转换成字符串
            String s = new String(content, CharsetUtil.UTF_8);
            System.out.println(s);
            //可以查看buf的类型，会显示读写索引及容量
            System.out.println("buf的类型："+buf);

            System.out.println("readerIndex:"+buf.readerIndex());
            System.out.println("writerIndex:"+buf.writerIndex());
            System.out.println("capacity:"+buf.capacity());

            System.out.println("可读字节数："+buf.readableBytes());//9
            buf.readByte();
            System.out.println("可读字节数："+buf.readableBytes());//8

            System.out.println("得到区间字符集："+buf.getCharSequence(1,4,CharsetUtil.UTF_8));//ello

        }

    }
}
