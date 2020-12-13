package org.wmy.netty.simple;

import io.netty.util.NettyRuntime;

/**
 * @author wmy
 * @create 2020-12-12 23:03
 */
public class TestSystemCPUCore {
    public static void main(String[] args) {
        System.out.println(NettyRuntime.availableProcessors());
    }
}
