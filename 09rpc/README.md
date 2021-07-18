## 改造自定义 RPC 的程序，提交到 GitHub
### 自定义注解，用于注册接口代理到Spring Context
[@RemoteService](./rpcfx-core/src/main/java/indi/kurok1/rpcfx/client/RemoteService.java) 用于标记一个接口，并写明远程调用地址
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RemoteService {

    String serviceName();

    String url();

}
```
[@EnableRemoteService](./rpcfx-core/src/main/java/indi/kurok1/rpcfx/client/EnableRemoteService.java) 用于开启全局代理开关
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RemoteServiceRegistrar.class)
public @interface EnableRemoteService {

    String[] baskPackages();

}
```
[RemoteServiceRegistrar](./rpcfx-core/src/main/java/indi/kurok1/rpcfx/client/RemoteServiceRegistrar.java) 参考`Spring Data Repository`的方式，注册接口代理
```java
public class RemoteServiceRegistrar implements ImportBeanDefinitionRegistrar
        , BeanFactoryAware, ResourceLoaderAware, EnvironmentAware, BeanClassLoaderAware {
    //读取@EnableRemoteSerivce定义的packages,扫描每个接口,并注册成bean
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

    //对于一个package,扫描当前包下所以被@RemoteService标记过的接口，生成代理
    protected Set<RemoteServiceWrapper> scanPackageAndProcess(String basePackage);
}
```
[RemoteServiceInterceptor](./rpcfx-core/src/main/java/indi/kurok1/rpcfx/client/RemoteServiceInterceptor.java) 接口代理实现，拦截接口方法，实现远程调用
```java
public class RemoteServiceInterceptor implements MethodInterceptor {
    //依赖BeanFactory,通过依赖查找的方式查找HttpClient
    public RemoteServiceInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.httpClient = this.beanFactory.getBean(HttpClient.class);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //具体拦截逻辑
        //获取serviceName
        RemoteService remoteService = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RemoteService.class);
        if (remoteService == null)
            return null;

        String serviceName = remoteService.serviceName();
        if (StringUtils.isEmpty(serviceName))
            return null;

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(serviceName);
        request.setMethod(method.getName());
        request.setParams(invocation.getArguments());

        RpcfxResponse response = post(request, remoteService.url());
        if (response == null)
            return null;
        try {
            return response.getResult() == null ? null : JSON.parse(response.getResult().toString());
        } catch (Throwable t) {
            throw new RpcfxException(t);
        }
    }

    private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: "+reqJson);

        return this.httpClient.post(req, url);
    }
}
```

[NettyHttpClient](./rpcfx-core/src/main/java/indi/kurok1/rpcfx/http/NettyHttpClient.java) HTTP请求客户端的Netty实现

## 结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub
### 数据库设计
```mysql
CREATE TABLE `user`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `code`  varchar(50) NOT NULL COMMENT '用户编码',
    `name`  varchar(50) NULL COMMENT '用户名称',
    PRIMARY KEY (`id`),
    INDEX `code_index`(`code`) USING BTREE
)ENGINE=InnoDB;

CREATE TABLE `account`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `userId`    int(11) NOT NULL,
    `onHandQty`  decimal (15,6) NOT NULL COMMENT '账户金额',
    `lockedQty`  decimal (15,6) NOT NULL COMMENT '暂时锁定金额',
    `inTransitQty`  decimal (15,6) NOT NULL COMMENT '在途金额',
    PRIMARY KEY (`id`),
    INDEX `user_index`(`userId`) USING BTREE
)ENGINE=InnoDB;
```
这里建立`User`表和`Account`表，`Account`表与`User`表为一对一关系，同时，`Account`增加锁定金额和在途金额，用于表示已分配的部分和即将到账部分

### 接口定义
这里定义两个接口`UserService`和`AccountService`
```java
public interface AccountService {

    /**
     * 转账逻辑：
     *  T:
     *      1.检查fromUser和toUser是否存在账户，任意一个不存在则失败
     *      2.检查fromUser的账户是否有剩余，并且剩余数量大于等于转账金额
     *      3.fromUser的账户增加锁定数量，金额为转账金额
     *      4.toUser的账户增加在途数量，金额为转账金额
     *  COMMIT：
     *      1. fromUser的账户的已有金额和锁定金额均扣减掉转账金额
     *      2. toUser的账户的已有金额增加转账金额，在途金额扣减掉转账金额
     * @param fromUserId 原用户id
     * @param toUserId 目标用户id
     * @param totalQty 金额
     * @return
     */
    boolean transfer(Long fromUserId, Long toUserId, BigDecimal totalQty);

    Account getByUserId(Long userId);

}

public interface UserService {

    User getById(Long id);

}
```

### 接口实现和注册服务
[UserServiceImpl](./dubbo-hmily-service-provider/dubbo-hmily-user-service/src/main/java/indi/kurok1/rpc/user/service/UserServiceImpl.java) 用户服务实现

[AccountServiceImpl](./dubbo-hmily-service-provider/dubbo-hmily-account-service/src/main/java/indi/kurok1/rpc/account/service/AccountServiceImpl.java) 账户服务实现
#### `UserService`注册
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="user_service"/>


        <dubbo:registry protocol="zookeeper" address="localhost:2181"/>

        <dubbo:protocol name="dubbo" port="20886"
                        server="netty" client="netty"
                        charset="UTF-8" threadpool="fixed" threads="500"
                        queues="0" buffer="8192" accepts="0" payload="8388608"/>

        <dubbo:service interface="indi.kurok1.rpc.commons.api.UserService"
                       ref="userService" executes="20"/>
    
</beans>
```
#### `AccountService`注册
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="account_service"/>


        <dubbo:registry protocol="zookeeper" address="localhost:2181"/>

        <dubbo:protocol name="dubbo" port="20887"
                        server="netty" client="netty"
                        charset="UTF-8" threadpool="fixed" threads="500"
                        queues="0" buffer="8192" accepts="0" payload="8388608"/>

        <dubbo:service interface="indi.kurok1.rpc.commons.api.AccountService"
                       ref="accountService" executes="20"/>


        <dubbo:reference timeout="500000000"
                         interface="indi.kurok1.rpc.commons.api.UserService"
                         id="userService"
                         retries="0" check="false" actives="20"/>
</beans>
```

### 客户端调用
申明dubbo
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="client"/>


    <dubbo:registry protocol="zookeeper" address="localhost:2181"/>

    <dubbo:reference timeout="500000000" interface="indi.kurok1.rpc.commons.api.UserService"
                         id="userService"
                         retries="0" check="false" actives="20"/>
    <dubbo:reference timeout="500000000" interface="indi.kurok1.rpc.commons.api.AccountService"
                     id="accountService"
                     retries="0" check="false" actives="20"/>
</beans>
```
转账demo
```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-client.xml");
AccountService accountService = context.getBean(AccountService.class);
accountService.transfer(2L,1L,new BigDecimal(30));
```

