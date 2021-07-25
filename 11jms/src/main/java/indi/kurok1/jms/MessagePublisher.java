package indi.kurok1.jms;

import javax.jms.*;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.07.25
 */
public class MessagePublisher {

    private final Destination destination;
    private final MessageProducer producer;
    private final Session session;

    public MessagePublisher(Destination destination) throws JMSException {
        this.destination = destination;
        this.session = SessionProvider.getSession();
        this.producer = this.session.createProducer(this.destination);
    }

    public void sendTextMessage(String message) throws JMSException {
        TextMessage textMessage = this.session.createTextMessage(message);
        this.producer.send(textMessage);
    }

    //todo 其他消息实现

}
