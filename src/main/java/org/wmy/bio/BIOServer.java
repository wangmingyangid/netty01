package org.wmy.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wmy
 * @create 2020-12-06 11:16
 */

public class BIOServer {
    public static void main(String[] args) throws IOException {
        //创建一个线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        //创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");

        while (true){

            System.out.println("当前线程id："+Thread.currentThread().getId()+","+
                    "当前线程name："+Thread.currentThread().getName());

            //阻塞式方法，如果没有客户端连接会阻塞
            System.out.println("等待客户端连接...");
            final Socket socket = serverSocket.accept();
            System.out.println("一个客户端连接进来了...");

            //如果有客户端连接，就创建一个线程，与之通讯
            threadPool.execute(new Runnable() {
                public void run() {
                    handler(socket);
                }
            });
        }
    }
    public static void handler(Socket socket){
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];

            System.out.println("当前线程id："+Thread.currentThread().getId()+","+
                    "当前线程name："+Thread.currentThread().getName());

            while (true){
                //阻塞式方法，当客户端没有数据发过来时，会阻塞到该方法
                System.out.println("wait data...");
                int read = inputStream.read(bytes);
                System.out.println("read data...");

                if(read != -1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //异常发生时关闭与客户端的连接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
