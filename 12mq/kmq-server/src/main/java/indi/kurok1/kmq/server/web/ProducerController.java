package indi.kurok1.kmq.server.web;

import indi.kurok1.kmq.core.TextMessage;
import indi.kurok1.kmq.server.QueueRegistry;
import indi.kurok1.kmq.server.core.Queue;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 面向生产者的接口
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@RestController
@RequestMapping("/api/producer")
public class ProducerController {


    private final QueueRegistry registry;

    public ProducerController(QueueRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("post")
    public ResponseEntity<String> postMessage(@RequestBody TextMessage message) {
        if (!StringUtils.hasLength(message.getQueue()))
            return ResponseEntity.status(500).body("queue must be not empty");

        String queueName = message.getQueue();
        if (!registry.containsKey(queueName))
            return ResponseEntity.status(500).body(String.format("queue [%s] not found", queueName));
        Queue queue = this.registry.get(queueName);
        queue.receive(message);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("resend")
    public ResponseEntity<String> resendMessage(@RequestBody TextMessage message) {
        if (!StringUtils.hasLength(message.getQueue()))
            return ResponseEntity.status(500).body("queue must be not empty");

        String queueName = message.getQueue();
        if (!registry.containsKey(queueName))
            return ResponseEntity.status(500).body(String.format("queue [%s] not found", queueName));
        Queue queue = this.registry.get(queueName);
        queue.resend(message);

        return ResponseEntity.ok("ok");
    }



}
