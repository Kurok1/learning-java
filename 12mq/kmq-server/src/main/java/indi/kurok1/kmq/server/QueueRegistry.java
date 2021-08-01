package indi.kurok1.kmq.server;

import indi.kurok1.kmq.server.core.Queue;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 队列注册中心
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.08.01
 */
@Component
public final class QueueRegistry extends ConcurrentHashMap<String, Queue> {



}
