package indi.kurok1.kafka.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

/**
 * TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@RestController
@RequestMapping("/api/kafka/rest")
public class KafkaController {

    private final KafkaTemplate kafkaTemplate;

    public KafkaController(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("send/{topic}")
    public String sendSync(@PathVariable("topic") String topic,  @RequestBody String message) {
        ListenableFuture future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {

            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
        return "OK";
    }

}
