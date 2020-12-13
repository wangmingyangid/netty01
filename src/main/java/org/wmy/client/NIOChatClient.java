package org.wmy.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author wmy
 * @create 2020-12-09 18:14
 */
public class NIOChatClient {

    private SocketChannel socketChannel;
    private Selector selector;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 7000;

    public NIOChatClient(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(HOST,PORT));
            selector =Selector.open();
            //监听读事件；当服务端有消息返回时，读取处理
            socketChannel.register(selector,SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMsg(){
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String str = scanner.next();
            ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
            try {
                socketChannel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveMsg(){

        try {
            System.out.println("没有消息接受，我被阻塞了....");
            selector.select();
            System.out.println("收到消息了.....");

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    System.out.println("收到的消息是"+new String(buffer.array()));
                }
                iterator.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        NIOChatClient client = new NIOChatClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    client.receiveMsg();
                }
            }
        }).start();
        while(!client.socketChannel.finishConnect());
        client.sendMsg();
    }
}
