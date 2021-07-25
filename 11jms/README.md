# 配置 redis 的主从复制，sentinel 高可用，Cluster 集群。
三个redis实例
* redis-0 `localhost:6379`
* redis-1 `localhost:6380`
* redis-2 `localhost:6381`

## 主从复制
`redis-0`作为主库, `redis-1`,`redis-2`作为从库

#### 从库配置
```text
slaveof 127.0.0.1 6379
```

## sentinel 高可用
#### 从库配置
```text
slaveof 127.0.0.1 6379
```

#### sentinel配置
```text
daemonize yes
logfile /home/ubuntu/redis/redis-4.0.8/redis-sentinel.log

sentinel monitor master 127.0.0.1 6379 2
sentinel down-after-milliseconds master 60000
sentinel failover-timeout master 180000
sentinel parallel-syncs master 1
```

## Cluster 集群
每个节点配置文件
```text
# 设置后台启动
daemonize yes
# 开启集群模式
cluster-enabled yes
# 集群节点超时时间(毫秒)
cluster-node-timeout 15000
```
启动每个节点
```shell
./redis-cli --cluster create localhost:6379 localhost:6380 localhost:6381 --cluster-replicas 1
```
验证
```shell
$ ./redis-cli -c -p 6379
127.0.0.1:6379> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
...
```

# 搭建 ActiveMQ 服务，基于 JMS，写代码分别实现对于 queue 和 topic 的消息生产和消费，代码提交到 github。
## 定义`SessionProvier`
代码位置[SessionProvider](./src/main/java/indi/kurok1/jms/SessionProvider.java)
```java
public class SessionProvider {

    private final static String ENDPOINT
            = "tcp://127.0.0.1:61616";


    private static ActiveMQConnection connection = null;

    private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<>();

    protected void initConnection() throws JMSException;

    public static Session getSession();

    public static void reset();

}
```

## 定义消息发布者
[MessagePublisher](./src/main/java/indi/kurok1/jms/MessagePublisher.java)
```java
public class MessagePublisher {

    private final Destination destination;
    private final MessageProducer producer;
    private final Session session;

    public MessagePublisher(Destination destination) throws JMSException {
        this.destination = destination;
        this.session = SessionProvider.getSession();
        this.producer = this.session.createProducer(this.destination);
    }

    public void sendTextMessage(String message) throws JMSException {
        TextMessage textMessage = this.session.createTextMessage(message);
        this.producer.send(textMessage);
    }

    //todo 其他消息实现

}
```

## 定义消息订阅者(线程)
[MessageSubscriber](./src/main/java/indi/kurok1/jms/MessageSubscriber.java)

## 测试代码
#### 测试topic
```java
@Test
public void testTopic() throws Exception {
    Destination destination = new ActiveMQTopic("test.topic");
    MessagePublisher publisher = new MessagePublisher(destination);
    for (int i=0 ; i < 10 ; i++) {
        Thread subscriberThread = new Thread(new MessageSubscriber(destination, message -> {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.printf("thread: [%s] received message : %s \n", Thread.currentThread().getName(), textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
        subscriberThread.start();
    }
    publisher.sendTextMessage("Hello World");
}
```

#### 测试queue
```java
@Test
public void testQueue() throws Exception {
    Destination destination = new ActiveMQQueue("test.queue");
    MessagePublisher publisher = new MessagePublisher(destination);
    for (int i=0 ; i < 10 ; i++) {
        Thread subscriberThread = new Thread(new MessageSubscriber(destination, message -> {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.printf("thread: [%s] received message : %s \n", Thread.currentThread().getName(), textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
        subscriberThread.start();
    }
    publisher.sendTextMessage("Hello World");
}
```