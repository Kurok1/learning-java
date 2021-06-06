## 1.使 Java 里的动态代理，实现一个简单的 AOP。
定义接口
```java
public interface Student {

    void read();

}
```
实现动态代理
```java
public class StudentHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("read".equals(method.getName())) {
            System.out.println("before read");
            System.out.println("reading");
            System.out.println("after read");
        }
        return proxy;
    }
}
```
测试代码[JdkProxyTestApplication](./src/main/java/indi/kurok1/jdkProxy/JdkProxyTestApplication.java)

## 2.写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）
[入口](./src/main/java/indi/kurok1/spring)

2.1 使用`Component`方式
2.2 使用`@Bean`方式
```java
@Bean("student_bean")
public Student student() {
    return new Student();
}
```
2.3 使用XML方式[application-context.xml](./src/main/resources/application-context.xml)
```xml
<context:component-scan base-package="indi.kurok1.spring" />

    <bean id="student_xml" class="indi.kurok1.spring.bean.Student">
        <property name="id" value="2"/>
        <property name="name" value="name"/>
    </bean>
```
2.4使用`BeanFactory`,注意这里只能注册到一级缓存
```java
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-context.xml");
//使用BeanFactory动态注册Bean
Student student = new Student();
context.getBeanFactory().registerSingleton("student_factory", student);
```

## 3.给前面课程提供的 Student/Klass/School 实现自动配置和 Starter
[入口](./src/main/java/indi/kurok1/starter)

3.1 程序入口
```java
/**
 * 必做2 自动装配和自定义starter
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//因为模块中依赖了spring-boot-starter-jdbc，但是这里没有使用到，避免报错，移除
@Import(SchoolAutoConfiguration.class)
public class SpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterApplication.class, args);
    }

}
```
3.2 自动装配

[ClassroomAutoConfiguration](./src/main/java/indi/kurok1/starter/autoconfigure/ClassroomAutoConfiguration.java)

[SchoolAutoConfiguration](./src/main/java/indi/kurok1/starter/autoconfigure/SchoolAutoConfiguration.java)

[StudentAutoConfiguration](./src/main/java/indi/kurok1/starter/autoconfigure/StudentAutoConfiguration.java)

[spring.factories](./src/main/resources/META-INF/spring.factories)

3.2 注入
```java
@EnableConfigurationProperties(School.class)
@ConditionalOnClass(School.class)
public class SchoolAutoConfiguration


@Import(SchoolAutoConfiguration.class)
```
```java
@ConditionalOnClass(Classroom.class)
@AutoConfigureAfter(SchoolAutoConfiguration.class)
public class ClassroomAutoConfiguration
//spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
        indi.kurok1.starter.autoconfigure.ClassroomAutoConfiguration
```
```java
@Configuration
public class StudentAutoConfiguration
```

## 4.研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法
4.1 连接提供 [ConnectionProvider](./src/main/java/indi/kurok1/jdbc/ConnectionProvider.java)
```java
public class ConnectionProvider extends HikariDataSource {

    private final HikariConfig config;

    public ConnectionProvider() {
        config = null;//不使用连接池
    }

    public ConnectionProvider(HikariConfig configuration) {
        super(configuration);//使用连接池
        this.config = configuration;

    }

    public Connection getConnection(String url, String username, String password) throws SQLException {
        if (this.config != null)
            throw new UnsupportedOperationException();

        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.config == null)
            throw new UnsupportedOperationException();
        else return super.getConnection();
    }
}
```

4.2 [JdbcService](./src/main/java/indi/kurok1/jdbc/service/JdbcService.java)
```java
protected final String url = "jdbc:mysql://localhost:3306/test_jdbc?serverTimezone=GMT%2B8";
protected final String username = "root";
protected final String password = "123qwertyA";

protected final ConnectionProvider provider;

public JdbcService() {
        this(false);
        }

public JdbcService(boolean usePool) {
        if (!usePool)
        this.provider = new ConnectionProvider();
        else this.provider = new ConnectionProvider(buildHikariConfig());
        }

private HikariConfig buildHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(this.url);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
        }

public Student insert(Student student, boolean beginTransaction)
public int delete(long id, boolean beginTransaction)
public int update(Student student, boolean beginTransaction)
public List<Student> findAll()
public int[] batchUpdate(Collection<Student> students)
```

4.3 使用连接池[HikariJdbcService](./src/main/java/indi/kurok1/jdbc/service/HikariJdbcService.java)
```java
public class HikariJdbcService extends JdbcService {

    public HikariJdbcService() {
        super(true);
    }


    @Override
    protected Connection getConnection() throws SQLException {
        return super.provider.getConnection();
    }
}
```

4.ext 基于 AOP 和自定义注解，实现 @MyCache(60) 对于指定方法返回值缓存 60 秒

4.ext.1
引入Spring Boot
```java
/**
 * 挑战: 基于 AOP 和自定义注解，实现 @MyCache(60) 对于指定方法返回值缓存 60 秒。
 *
 * 这里依赖Spring Boot
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.06.06
 */
@SpringBootApplication(scanBasePackages = {"indi.kurok1.jdbc"}, exclude = DataSourceAutoConfiguration.class)//因为模块中依赖了spring-boot-starter-jdbc，但是这里没有使用到，避免报错，移除
public class CachedHikariApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachedHikariApplication.class, args);
    }

}
```

4.ext.2 定义`MyCache`和`CacheAbility`
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyCache {

    int expiredMillions() default 60 * 1000;

}
```
[CacheAbility](./src/main/java/indi/kurok1/jdbc/cache/CacheAbility.java)

4.ext.3 实现缓存提供者(内存实现)并注入
[MemoryCacheProvider](./src/main/java/indi/kurok1/jdbc/cache/MemoryCacheProvider.java)
```java
@Component
public class MemoryCacheProvider implements CacheAbility
        private final ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>(256);
        private final ConcurrentHashMap<String, Long> expiredMap = new ConcurrentHashMap<>(256);
```

4.ext.4 aop拦截方法 [DataQueryInterceptor](./src/main/java/indi/kurok1/jdbc/aop/DataQueryInterceptor.java)
```java
public class DataQueryInterceptor {

    private final CacheAbility cacheProvider;

    @Autowired
    public DataQueryInterceptor(CacheAbility cacheAbility) {
        this.cacheProvider = cacheAbility;
    }

    private static final Logger logger = LoggerFactory.getLogger(DataQueryInterceptor.class);

    private Class<MyCache> getAnnotation() { return MyCache.class; }

    @Around(value = "execution(* indi.kurok1.jdbc.service.*Service.*(..))")
    public Object before(ProceedingJoinPoint joinPoint) throws RuntimeException {
        //todo
    }

}
```

## 5.网关的 frontend/backend/filter/router 线程池都改造成 Spring 配置方式；
[自动装配](./src/main/java/indi/kurok1/gateway/autoconfigure)

[GatewayAutoConfiguration](./src/main/java/indi/kurok1/gateway/autoconfigure/GatewayAutoConfiguration.java)

## 6.基于 AOP 改造 Netty 网关，filter 和 router 使用 AOP 方式实现；

[FilterInterceptor](./src/main/java/indi/kurok1/gateway/aop/FilterInterceptor.java)

[RouteInterceptor](./src/main/java/indi/kurok1/gateway/aop/RouteInterceptor.java)

## 7.基于前述改造，将网关请求前后端分离，中级使用 JMS 传递消息；
没空了,,ԾㅂԾ,, 有时间补上