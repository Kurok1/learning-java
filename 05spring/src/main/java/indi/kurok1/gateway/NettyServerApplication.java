package indi.kurok1.gateway;

import indi.kurok1.gateway.autoconfigure.EnableRequestFilter;
import indi.kurok1.gateway.autoconfigure.EnableResponseFilter;
import indi.kurok1.gateway.autoconfigure.GatewayProperties;
import indi.kurok1.gateway.inbound.HttpInboundServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

/**
 * Netty Server Application
 *
 * 挑战: 整合Spring
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
@SpringBootApplication(scanBasePackages = "indi.kurok1.gateway", exclude = DataSourceAutoConfiguration.class)
@EnableRequestFilter
@EnableResponseFilter
public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "3.0.0";

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NettyServerApplication.class, args);
        GatewayProperties properties = context.getBean(GatewayProperties.class);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = context.getBean(HttpInboundServer.class);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + properties.getPort() + " for server:" + server.toString());
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
