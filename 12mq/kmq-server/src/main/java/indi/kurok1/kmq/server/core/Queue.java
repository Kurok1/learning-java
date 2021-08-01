package indi.kurok1.kmq.server.core;

import indi.kurok1.kmq.core.Message;

import java.io.Closeable;
import java.util.Collection;

/**
 * 队列抽象接口
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
public interface Queue extends Closeable {

    /**
     * @return 队列名称
     */
    String getName();

    /**
     * 从队列中抓取消息
     * @param maxFetchSize 最大抓取消息个数
     * @return 消息集合
     */
    Collection<Message> fetch(int maxFetchSize);

    /**
     * 接受消息
     * @param message 消息
     * @return 返回偏移量
     */
    int receive(Message message);

    /**
     * 消息确认
     * @param message 消息
     * @return 确认成功与否
     */
    boolean confirm(Message message);

    /**
     * 消息重发
     * @param message 需要重发的消息
     */
    void resend(Message message);

}
