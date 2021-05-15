package indi.kurok1.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 多线程版本的httpserver，每次请求都新创建一个线程来处理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class NewThreadHttpServer {

    public static void main(String[] args) throws IOException {
        int port = 8082;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            new Thread(new HttpThread(serverSocket.accept())).start();
        }
    }

}

class HttpThread implements Runnable {

    private final Socket socket;

    public HttpThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                response(this.socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void response(Socket accept) throws IOException {
        OutputStream outputStream = accept.getOutputStream();
        StringBuffer response = new StringBuffer();
        response.append("HTTP/1.1 200 OK\n");
        response.append("Content-Type:text/html;charset=utf-8\n");
        String body = "Hellp Http Server";
        response.append("Content-Length:" + body.getBytes().length);
        response.append("\n\n");
        response.append(body);
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
