package indi.kurok1.gateway;

import indi.kurok1.gateway.inbound.HttpInboundServer;

import java.util.Arrays;

/**
 * Netty Server Application
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "3.0.0";

    public static void main(String[] args) {

        String proxyPort = System.getProperty("proxyPort","8888");


        // 这是多个后端url走随机路由的例子
        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + server.toString());
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
