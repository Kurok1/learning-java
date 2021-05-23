package indi.kurok1.gateway.route;


import java.util.List;

/**
 * 服务实例选择器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public interface ServiceChooser {

    /**
     * 从众多实例中选择一个
     * @param serviceInstances 可选的服务列表
     * @param serviceName 服务名称
     * @return 如果服务实例列表的数据为空，返回null
     */
    default ServiceInstance choose(List<ServiceInstance> serviceInstances, String serviceName) {
        if (serviceInstances == null || serviceInstances.size() == 0)
            return null;

        return serviceInstances.get(0);//默认返回第一个服务实例
    }

}
