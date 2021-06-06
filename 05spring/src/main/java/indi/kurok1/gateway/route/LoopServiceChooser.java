package indi.kurok1.gateway.route;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询服务选择
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class LoopServiceChooser implements ServiceChooser {

    private final static ConcurrentHashMap<String, AtomicInteger> serviceIndexMap = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance choose(List<ServiceInstance> serviceInstances, String serviceName) {
        if (!serviceIndexMap.containsKey(serviceName)) {
            serviceIndexMap.put(serviceName, new AtomicInteger(0));
        }
        final AtomicInteger index = serviceIndexMap.get(serviceName);
        if (serviceInstances == null || serviceInstances.isEmpty())
            return null;
        int size = serviceInstances.size();
        if (size == 1)
            return serviceInstances.get(0);

        synchronized (LoopServiceChooser.class) {
            if (index.get() == size)
                index.set(0);
            return serviceInstances.get(index.getAndIncrement());
        }
    }
}
