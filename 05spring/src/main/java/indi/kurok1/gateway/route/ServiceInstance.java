package indi.kurok1.gateway.route;

/**
 * 具体的服务实例，包含服务名称，ip地址，端口等等信息
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class ServiceInstance {

    private String serviceName;

    private String ipAddress;

    private int port;

    public ServiceInstance() {
    }

    public ServiceInstance(String serviceName, String ipAddress, int port) {
        this.serviceName = serviceName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

