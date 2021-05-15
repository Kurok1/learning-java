package indi.kurok1.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池实现的httpServer
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class ThreadPoolHttpServer {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(40);

    public static void main(String[] args) throws IOException {
        int port = 8083;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            executorService.submit(new HttpThread(serverSocket.accept()));
        }
    }

}
