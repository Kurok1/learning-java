package indi.kurok1.kmq.client;

import indi.kurok1.kmq.client.autoconfig.KmqClientProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费者管理
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@Component
public class Consumers implements BeanFactoryAware, InitializingBean {

    private final KmqClientProperties properties;

    private final ScheduledExecutorService executorService;

    private final Map<String, Consumer> consumerMap = new ConcurrentHashMap<>();

    private ListableBeanFactory beanFactory;

    public Consumers(KmqClientProperties properties) {
        this.properties = properties;
        if (this.properties.getQueues().size() > 0) {
            this.executorService = Executors.newScheduledThreadPool(this.properties.getQueues().size() * 2);
        } else {
            this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        }
    }

    public synchronized void addConsumer(String queue, Consumer consumer) {
        if (this.consumerMap.containsKey(queue))
            return;
        this.consumerMap.put(queue, consumer);
        //一秒一周期执行
        this.executorService.scheduleAtFixedRate(consumer, 0, 1,TimeUnit.SECONDS);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] names = this.beanFactory.getBeanNamesForType(Consumer.class);
        if (names != null && names.length > 0) {
            for (String name : names) {
                Consumer consumer = this.beanFactory.getBean(name, Consumer.class);
                addConsumer(consumer.getQueueName(), consumer);
            }
        }
    }
}
