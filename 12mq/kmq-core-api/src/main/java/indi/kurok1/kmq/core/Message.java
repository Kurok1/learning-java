package indi.kurok1.kmq.core;

import java.io.Serializable;

/**
 * 消息实体定义
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public interface Message<T extends Serializable> {

    /**
     * @return 消息id
     */
    String getId();

    MessageHeaders getHeaders();

    T getPayload();

    String getQueue();

}
