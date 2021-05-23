package indi.kurok1.gateway.route;

import indi.kurok1.gateway.common.ServiceUtils;
import indi.kurok1.gateway.outbound.HttpClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理入口请求，并且分发到对应的服务实例上
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.20
 */
public class Dispatcher {

    public final static Dispatcher INSTANCE = new Dispatcher();


    private final ServiceChooser serviceChooser;
    private final HttpClient httpClient;

    public Dispatcher() {
        initService();
        initMatchRule();
        serviceChooser = ServiceUtils.defaultImplement(ServiceChooser.class, new ServiceChooser(){});
        httpClient = ServiceUtils.defaultImplement(HttpClient.class);
    }

    private final ConcurrentHashMap<String, List<ServiceInstance>> instanceMap = new ConcurrentHashMap<>();
    private final List<MatchRule> matchRules = new LinkedList<>();
    /**
     * 初始化服务列表
     * TODO 外部配置文件指定
     */
    private void initService() {
        instanceMap.put("application1", Arrays.asList(
                new ServiceInstance("application1", "localhost", 8088)
        ));
        instanceMap.put("application2", Arrays.asList(
                new ServiceInstance("application2", "localhost", 9094),
                new ServiceInstance("application2", "localhost", 9095),
                new ServiceInstance("application2", "localhost", 9096)
        ));
        instanceMap.put("application3", Arrays.asList(
                new ServiceInstance("application3", "localhost", 9097),
                new ServiceInstance("application3", "localhost", 9098),
                new ServiceInstance("application3", "localhost", 9099)
        ));
    }

    /**
     * 初始化匹配规则
     * todo 外部数据库初始化，根据order排序，order越小越考前
     */
    private void initMatchRule() {
        MatchRule rule = new MatchRule();
        rule.setMatchRuleValue("/api/hello");
        rule.setOrder(0);
        rule.setId(1L);
        rule.setServiceName("application1");
        matchRules.add(rule);
    }

    /**
     * 分发请求到服务实例
     * @param httpRequest 入口接收的http请求
     * @param context 上下文
     * @param content 发送给服务方的请求数据
     */
    public void dispatch(FullHttpRequest httpRequest, ChannelHandlerContext context, Object content) {
        String appName = "";

        if (matchRules.size() > 0) {
            for (MatchRule matchRule : matchRules) {
                if (matchRule.matched(httpRequest.uri())) {
                    appName = matchRule.getServiceName();
                    break;
                }
            }
        }

        List<ServiceInstance> instances = instanceMap.get(appName);
        if (instances == null || instances.isEmpty()) {
            throw new NullPointerException();
        }
        //实例选择
        ServiceInstance instance = serviceChooser.choose(instances, appName);
        if (instance == null)
            throw new NullPointerException();

        Map<String, String> headers = new HashMap<>();
        if (httpRequest.headers().size() > 0) {
            httpRequest.headers().forEach(
                    (it)-> headers.put(it.getKey(), it.getValue())
            );
        }

        switch (httpRequest.method().name()) {
            case "GET":
                httpClient.get(instance, httpRequest.uri(), headers, context);break;
            case "POST":
                httpClient.post(instance, httpRequest.uri(), content, headers, context);break;
            case "PUT":
                httpClient.put(instance, httpRequest.uri(), content, headers, context);break;
            case "DELETE":
                httpClient.delete(instance, httpRequest.uri(), content, headers, context);break;
            default:
                throw new IllegalStateException("Unexpected value: " + httpRequest.method().name());
        }
    }



}
