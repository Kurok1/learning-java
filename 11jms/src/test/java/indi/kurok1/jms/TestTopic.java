package indi.kurok1.jms;

import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.25
 */
public class TestTopic {

    @Test
    public void testTopic() throws Exception {
        Destination destination = new ActiveMQTopic("test.topic");
        MessagePublisher publisher = new MessagePublisher(destination);
        for (int i=0 ; i < 10 ; i++) {
            Thread subscriberThread = new Thread(new MessageSubscriber(destination, message -> {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.printf("thread: [%s] received message : %s \n", Thread.currentThread().getName(), textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }));
            subscriberThread.start();
        }


        publisher.sendTextMessage("Hello World");

    }


}
