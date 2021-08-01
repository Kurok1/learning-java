package indi.kurok1.kmq.server.web;

import indi.kurok1.kmq.core.Message;
import indi.kurok1.kmq.core.TextMessage;
import indi.kurok1.kmq.server.QueueRegistry;
import indi.kurok1.kmq.server.core.Queue;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

/**
 * 面向消费者的接口
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@RestController
@RequestMapping("/api/consumer")
public class ConsumerController {

    private final QueueRegistry registry;

    public ConsumerController(QueueRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("get/{queueName}/{fetchSize}")
    public Collection<Message> get(@PathVariable("queueName") String queueName, @PathVariable("fetchSize") Integer fetchSize) {
        if (fetchSize == null || fetchSize < 0)
            return Collections.emptyList();

        if (!registry.containsKey(queueName))
            throw new IllegalArgumentException(String.format("queue [%s] not found", queueName));

        Queue queue = this.registry.get(queueName);
        return queue.fetch(fetchSize);
    }

    @PostMapping("confirm")
    public ResponseEntity<String> confirm(@RequestBody TextMessage message) {
        if (!StringUtils.hasLength(message.getQueue()))
            return ResponseEntity.status(500).body("queue must be not empty");

        String queueName = message.getQueue();
        if (!registry.containsKey(queueName))
            return ResponseEntity.status(500).body(String.format("queue [%s] not found", queueName));
        Queue queue = this.registry.get(queueName);
        queue.confirm(message);

        return ResponseEntity.ok("ok");
    }

}
