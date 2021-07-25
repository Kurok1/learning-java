package indi.kurok1.jms;

import javax.jms.*;
import java.util.function.Consumer;

/**
 * 消息订阅方
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.25
 */
public class MessageSubscriber implements MessageListener, Runnable {

    private final Destination destination;
    private final Session session;

    private final Consumer<Message> consumer;

    public MessageSubscriber(Destination destination, Consumer<Message> consumer) {
        this.destination = destination;
        this.consumer = consumer;
        this.session = SessionProvider.getSession();
    }

    @Override
    public void run() {
        try {
            MessageConsumer consumer = session.createConsumer(this.destination);
            consumer.setMessageListener(this);

            while (true) {
                //todo 阻塞线程
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(Message message) {
        this.consumer.accept(message);
    }
}
