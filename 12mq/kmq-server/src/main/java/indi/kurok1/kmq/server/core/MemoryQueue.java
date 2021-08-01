package indi.kurok1.kmq.server.core;

import indi.kurok1.kmq.core.Message;
import indi.kurok1.kmq.core.MessageHeader;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 内存实现的队列
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public class MemoryQueue implements Queue {

    private String name;

    private ArrayList<Message> queue;

    private AtomicInteger index = new AtomicInteger(0);

    public MemoryQueue(String name) {
        Assert.hasLength(name, "name must be not empty");
        this.name = name;
        this.queue = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int receive(Message message) {
        synchronized (this.queue) {
            queue.add(message);
            return queue.size() - 1;
        }
    }

    @Override
    public Collection<Message> fetch(int maxFetchSize) {
        int currentMessages = queue.size() - index.get() - 1;
        synchronized (this.queue) {
            if (currentMessages > maxFetchSize) {
                int beginIndex = index.get();
                Collection<Message> messages = this.queue.subList(beginIndex, beginIndex + maxFetchSize);
                index.set(beginIndex + maxFetchSize);
                return messages;
            }

            int beginIndex = index.get();
            Collection<Message> messages = this.queue.subList(beginIndex, this.queue.size());
            index.set(this.queue.size());
            return messages;
        }
    }

    @Override
    public boolean confirm(Message message) {
        //检查offset
        Integer offset = -1;
        try {
            offset = Integer.valueOf(message.getHeaders().getValue(MessageHeader.OFFSET));
        } catch (NumberFormatException e) {
            return false;
        }
        if (this.index.get() < offset)
            return false;
        //todo 确认操作
        return true;
    }

    @Override
    public void resend(Message message) {
        this.receive(message);
    }

    @Override
    public void close() throws IOException {
        this.index = null;
        this.queue.clear();
        this.queue = null;
    }
}
