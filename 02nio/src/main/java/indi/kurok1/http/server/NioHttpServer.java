package indi.kurok1.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * NIO实现的httpServer
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class NioHttpServer {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(40);

    public static void main(String[] args) throws IOException {
        int port = 8084;
        //创建一个channel
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        //绑定地址
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        socketChannel.bind(socketAddress);
        //非阻塞式io
        socketChannel.configureBlocking(false);

        //设置一个选择器
        Selector selector = Selector.open();
        //channel注册到选择器
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //阻塞队列，存放异步结果
        BlockingQueue<Future<?>> readableQueue = new ArrayBlockingQueue<>(40);
        ForkJoinPool forkJoinPool = new ForkJoinPool(40);
        while (true) {
            //查询3秒内selector中被命中的事件
            if (selector.select(1000) == 0) {
                //3秒没有接收到事件
                continue;
            }

            //获取到被命中的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Set<SelectionKey> reabledKeys = new HashSet<>();

            for (SelectionKey selectionKey : selectionKeys) {
                if (selectionKey.isAcceptable()) {
                    //创建一个连接
                    SocketChannel channel = socketChannel.accept();
                    channel.configureBlocking(false);
                    //连接绑定选择器，并创建一个对应的缓冲区
                    channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));//等待读
                }
                if (selectionKey.isReadable()) {
                    reabledKeys.add(selectionKey);
                }
                if (reabledKeys.size() > 0) {
                    reabledKeys.parallelStream().forEach(
                            (key)->{
                                //获取通道
                                SocketChannel channel = (SocketChannel) key.channel();
                                //获取关联的ByteBuffer
                                ByteBuffer buffer = (ByteBuffer) key.attachment();
                                //打印从客户端获取到的数据
                                try {
                                    channel.read(buffer);
                                    buffer.clear();
                                    //读取到之后，给客户端反馈
                                    channel.write(ByteBuffer.wrap(response()));
                                    channel.close();
                                } catch (IOException e) {
                                }
                            }
                    );
                }

            }
            selectionKeys.clear();
        }

    }

    private static byte[] response() throws IOException {
        StringBuffer response = new StringBuffer();
        response.append("HTTP/1.1 200 OK\n");
        response.append("Content-Type:text/html;charset=utf-8\n");
        String body = "Hellp Http Server";
        response.append("Content-Length:" + body.getBytes().length);
        response.append("\n\n");
        response.append(body);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}

