package indi.kurok1.kmq.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 文本消息负载
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 * @see Message
 */
public final class TextMessage implements Message<String> {

    private String payload;

    private transient Charset charset;

    private MessageHeaders headers;

    private String id;

    private String queue;

    public TextMessage() {
    }

    public TextMessage(String text, Charset charset) {
        this.payload = text;
        this.charset = charset;
    }

    public TextMessage(String text) {
        this(text, StandardCharsets.UTF_8);
    }

    @Override
    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public MessageHeaders getHeaders() {
        return this.headers;
    }

    public void setHeaders(MessageHeaders headers) {
        this.headers = headers;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getQueue() {
        return this.queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
