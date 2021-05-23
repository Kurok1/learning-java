## 1.整合OkHttp/HttpClient

#### 1.1 定义抽象类[`HttpClient`](./src/main/java/indi/kurok1/gateway/outbound/HttpClient.java)

```java
public void get(ServiceInstance serviceInstance, String uri, Map<String, String> httpHeaders, ChannelHandlerContext context);
public void post(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context);
public void post(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context);
public void post(ServiceInstance serviceInstance, String uri, Object requestBody, Map<String, String> httpHeaders, ChannelHandlerContext context);
```

其中`ChannelHandlerContext`用于回写服务提供方数据

#### 1.2 OkHttp整合，[实现代码](./src/main/java/indi/kurok1/gateway/outbound/okhttp/HttpClientImpl.java)



## 2.使用 netty 实现后端 http 访问

具体代码实现 [NettyHttpClient](./src/main/java/indi/kurok1/gateway/outbound/netty/NettyHttpClient.java)



## 3.实现过滤器。

#### 3.1 HttpRequestFilter

`HttpRequestFilter`用于过滤请求方http请求，是一个接口

```java
public interface HttpRequestFilter {

    /**
     * 过滤请求
     * @param request http请求
     * @param ctx
     * @return 如果返回true，表示继续过滤下去，否则直接关闭流程，请确保返回false时已经写入了响应数据
     */
    boolean filter(HttpRequest request, ChannelHandlerContext ctx);

}
```

`HttpRequest`封装了对应的http请求



#### 3.2 HttpResponseFilter

`HttpResponseFilter`用于过滤服务提供方的http响应，也是一个接口

```java
public interface HttpResponseFilter {

    boolean filter(HttpResponse response, ChannelHandlerContext ctx);

}
```

`HttpResponse`封装了服务调用方的http响应，同时通过返回值判断是否继续过滤下去



#### 3.3 SPI注入

`HttpRequestFilter`和`HttpResponseFilter`均通过`SPI`的方式注入，[服务描述文件位置](./src/main/resources/META-INF/services)

```
# META-INF/services/indi.kurok1.gateway.filter.HttpRequestFilter
indi.kurok1.gateway.filter.TokenHttpRequestFilter
```

[服务注入工具类](./src/main/java/indi/kurok1/gateway/common/ServiceUtils.java)

```java
public class ServiceUtils {

    public static <S> S defaultImplement(Class<S> requiredType) {
        Iterable<S> serviceLoader = allImplement(requiredType);
        Iterator<S> iterator = serviceLoader.iterator();
        //取第一个
        if (!iterator.hasNext())
            throw new IllegalArgumentException("no default implement found : " + requiredType.getName());
        return iterator.next();
    }

    public static <S> S defaultImplement(Class<S> requiredType, S defaultService) {
        Iterable<S> serviceLoader = allImplement(requiredType);
        Iterator<S> iterator = serviceLoader.iterator();
        //找第一个，没有返回默认实现
        if (!iterator.hasNext())
            return defaultService;
        return iterator.next();
    }

    public static <S> Iterable<S> allImplement(Class<S> requiredType) {
        //返回所有实现
        return ServiceLoader.load(requiredType, ServiceUtils.class.getClassLoader());
    }


}
```

使用demo

```java
private Iterable<HttpRequestFilter> filters = ServiceUtils.allImplement(HttpRequestFilter.class);;
if (filters != null) {
    //过滤请求
	for (HttpRequestFilter filter : filters) {
    	boolean needFilter = filter.filter(fullRequest, ctx);
        if (!needFilter)
        	return;
        }

    }
}
```



## 4.路由实现

#### 4.1 路由匹配规则

[具体实现代码](./src/main/java/indi/kurok1/gateway/route/MatchRule.java)

```java
public class MatchRule {

    private long id;
    private String matchRuleValue;
    private String type = Type.DEFAULT;//匹配类型
    private String serviceName;
    private int order;//排序，数字越小优先级越高
    
    //... getter and setter
    
    /**
     * 判断一个uri是否匹配
     * @param uri
     * @return 是否匹配成功
     */
    public boolean matched(String uri);
    
    public static class Type {
        public static final String LIKE = "LIKE";//包含
        public static final String REGEX = "REGEX";//正则
        public static final String EQUALS = "EQUALS";//完全相等
        public static final String DEFAULT = EQUALS;//默认=完全相等
    }
}
```

可以考虑实例化到数据库中长期保存

#### 4.2 路由选择

定义[服务实例](./src/main/java/indi/kurok1/gateway/route/ServiceInstance.java)

```java
public class ServiceInstance {

    private String serviceName;

    private String ipAddress;

    private int port;
    
    //... getter and setter
    
}
```

一个服务可能会有多个实例，即一个`serviceName`下可以在注册在多个主机多个端口上，一个实例只有一个对应的`serviceName`

[路由选择器](./src/main/java/indi/kurok1/gateway/route/ServiceChooser.java)

```java
ServiceInstance choose(List<ServiceInstance> serviceInstances, String serviceName)
```

从外部传参的服务实例列表中选择一个可用的返回

接口实现: 

[`RandomServiceChooser`](./src/main/java/indi/kurok1/gateway/route/RandomServiceChooser.java) 从列表中随机选择一个返回

[`LoopServiceChooser`](./src/main/java/indi/kurok1/gateway/route/LoopServiceChooser.java) 采用全局下标的方式，轮询服务列表，返回服务实例

通过`SPI`的方式进行注入

```
# META-INF/services/indi.kurok1.gateway.route.ServiceChooser
indi.kurok1.gateway.route.RandomServiceChooser
indi.kurok1.gateway.route.LoopServiceChooser
```



#### 4.3 请求分配

请求分配器实现: [`Dispatcher`](./src/main/java/indi/kurok1/gateway/route/Dispatcher.java)

```java
    private final ServiceChooser serviceChooser; //服务选择器
    private final HttpClient httpClient; //Htto请求客户端
    private final ConcurrentHashMap<String, List<ServiceInstance>> instanceMap = new ConcurrentHashMap<>(); //服务实例映射  服务名称->服务实例列表
    private final List<MatchRule> matchRules = new LinkedList<>(); //匹配规则

	/**
     * 分发请求到服务实例
     * @param httpRequest 入口接收的http请求
     * @param context 上下文
     * @param content 发送给服务方的请求数据
     */
    public void dispatch(FullHttpRequest httpRequest, ChannelHandlerContext context, Object content);
```

