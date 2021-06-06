package indi.kurok1.gateway.autoconfigure;

import indi.kurok1.gateway.route.ServiceInstance;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 网关相关配置
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private int port = 8888;


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private WorkerProperties worker = new WorkerProperties();

    public WorkerProperties getWorker() {
        return worker;
    }

    public void setWorker(WorkerProperties worker) {
        this.worker = worker;
    }

    public static class WorkerProperties {

        private int size = 16;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    private List<ServiceInstance> route = new CopyOnWriteArrayList<>();

    public List<ServiceInstance> getRoute() {
        return route;
    }

    public void setRoute(List<ServiceInstance> route) {
        this.route = route;
    }
}
