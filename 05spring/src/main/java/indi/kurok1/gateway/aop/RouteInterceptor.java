package indi.kurok1.gateway.aop;

import indi.kurok1.gateway.autoconfigure.GatewayProperties;
import indi.kurok1.gateway.route.MatchRule;
import indi.kurok1.gateway.route.ServiceChooser;
import indi.kurok1.gateway.route.ServiceInstance;
import io.netty.handler.codec.http.FullHttpRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Aspect
@Component
public class RouteInterceptor {

    private final GatewayProperties gatewayProperties;

    private final ServiceChooser serviceChooser;

    private final ConcurrentHashMap<String, List<ServiceInstance>> instanceMap = new ConcurrentHashMap<>();
    private final List<MatchRule> matchRules = new LinkedList<>();

    public RouteInterceptor(GatewayProperties gatewayProperties, ServiceChooser serviceChooser) {
        this.gatewayProperties = gatewayProperties;
        this.serviceChooser = serviceChooser;
        initMatchRule();
        initService();
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
     * 初始化服务列表
     * 外部配置文件指定
     */
    private void initService() {
        instanceMap.putAll(this.gatewayProperties.getRoute().stream().collect(Collectors.groupingBy(ServiceInstance::getServiceName)));
    }

    @Around(value = "execution(* indi.kurok1.gateway.route.Dispatcher.dispatch(io.netty.handler.codec.http.FullHttpRequest, io.netty.channel.ChannelHandlerContext, java.lang.Object, indi.kurok1.gateway.route.ServiceInstance))")
    public void before(ProceedingJoinPoint joinPoint) throws RuntimeException {
        String appName = "";

        Object[] args = joinPoint.getArgs();
        FullHttpRequest httpRequest = (FullHttpRequest) args[0];

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

        args = new Object[]{httpRequest, args[1], args[2], instance};

        try {
            joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
