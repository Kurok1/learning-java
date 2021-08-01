package indi.kurok1.kmq.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * KMQ引导Server
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@SpringBootApplication(scanBasePackages = {"indi.kurok1.kmq.server"})
public class KmqServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmqServerApplication.class, args);
    }

}
