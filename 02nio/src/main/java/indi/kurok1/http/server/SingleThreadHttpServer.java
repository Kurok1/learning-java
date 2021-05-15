package indi.kurok1.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * HTTP server 单线程实现阻塞
 * 启动参数-Xmx1g -Xms1g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.15
 */
public class SingleThreadHttpServer {

    public static void main(String[] args) throws IOException {
        //绑定8081端口
        int port = 8081;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket accept = serverSocket.accept();
            response(accept);
        }
    }

    private static void response(Socket accept) throws IOException {
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
