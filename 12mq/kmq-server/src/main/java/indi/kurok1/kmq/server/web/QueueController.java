package indi.kurok1.kmq.server.web;

import indi.kurok1.kmq.server.QueueRegistry;
import indi.kurok1.kmq.server.core.MemoryQueue;
import indi.kurok1.kmq.server.core.Queue;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 队列访问api
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@RestController
@RequestMapping("/api/queue")
public class QueueController {


    private final QueueRegistry registry;

    public QueueController(QueueRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/create/{queueName}")
    public String create(@PathVariable("queueName") String queueName) {
        if (this.registry.containsKey(queueName))
            throw new IllegalStateException(String.format("the queue [%s] existed", queueName));

        this.registry.put(queueName, new MemoryQueue(queueName));
        return "ok";
    }

    @DeleteMapping("delete/{queueName}")
    public String delete(@PathVariable("queueName") String queueName) throws IOException {
        if (!this.registry.containsKey(queueName))
            throw new IllegalStateException(String.format("the queue [%s] not existed", queueName));

        Queue queue = this.registry.get(queueName);
        queue.close();
        return "ok";
    }

}
