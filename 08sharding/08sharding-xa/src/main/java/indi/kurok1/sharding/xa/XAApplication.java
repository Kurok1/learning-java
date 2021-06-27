package indi.kurok1.sharding.xa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.27
 */
@SpringBootApplication
public class XAApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(XAApplication.class, args);
        OrderService orderService = run.getBean(OrderService.class);
        orderService.process();
    }

}
