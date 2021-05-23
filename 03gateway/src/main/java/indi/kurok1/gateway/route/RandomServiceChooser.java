package indi.kurok1.gateway.route;

import java.util.List;
import java.util.Random;

/**
 * 随机服务实例选择
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class RandomServiceChooser implements ServiceChooser {

    @Override
    public ServiceInstance choose(List<ServiceInstance> serviceInstances, String serviceName) {
        if (serviceInstances == null || serviceInstances.isEmpty())
            return null;
        int size = serviceInstances.size();
        if (size == 1)
            return serviceInstances.get(0);

        Random random = new Random(System.currentTimeMillis());
        return serviceInstances.get(random.nextInt(size));
    }
}
