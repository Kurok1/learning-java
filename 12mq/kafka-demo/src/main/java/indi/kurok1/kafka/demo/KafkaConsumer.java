package indi.kurok1.kafka.demo;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 卡夫卡消费
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@Component
public class KafkaConsumer {

    @KafkaListener(
            groupId = "consumer",
            topicPartitions = {
                    @TopicPartition(
                            topic = "topic1",
                            partitions = {"0"}
                    )
            }
    )
    public void onMessageForPartition1(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println(
                "Received Message: " + message + "from partition: " + partition);
    }

    @KafkaListener(
            groupId = "consumer",
            topicPartitions = {
                    @TopicPartition(
                            topic = "topic1",
                            partitions = {"1"}
                    )
            }
    )
    public void onMessageForPartition2(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println(
                "Received Message: " + message + "from partition: " + partition);
    }

}
