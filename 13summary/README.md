## 分别用 100 个字以上的一段话，加上一幅图 (架构图或脑图)，总结自己对下列技术的关键点思考和经验认识:

### 1)JVM
[JVM](./JVM.pdf)

JVM是Java程序的运行环境
* 类加载
  
基于双亲委派机制，将二进制字节流转化为运行时的数据结构。JVM默认实现BootstrapClassLoader(java程序中无法获取)，java应用中往往是基于URLClassLoader完成类加载的
* 内存区域划分
  
JVM内部将内存区域划分为，堆空间，栈空间，元信息区。其中堆空间是存放实际的java对象，堆空间划分为新生代和老年代，比例默认为1:2,其中新生代划分成eden区和两个s区，比例默认为8:1:1
* 垃圾回收
  
由于内存空间有限，因此需要按照一定的策略回收不再被使用的对象
### 2)NIO
NIO的本质是基于事件机制的多路复用IO
* 对比传统BIO
  
BIO是最传统的IO模型，在建立连接后会阻塞当前线程等待输入，这样对cpu造成了不必要的性能开销，同时也限制了服务的吞吐量。为了优化这种问题，NIO采用事件监听机制，将一次IO请求划分成4种事件
1. 连接建立
2. 读准备
3. 写准备
4. 关闭连接
 
基于事件机制，可以实现线程复用，大大提升了服务的吞吐量
### 3) 并发编程
[并发](../04concurrent/src/main/resources/并发多线程.png)
* 线程的常用操作


1. Thread#start 启动一个线程
2. Thread#sleep 使当前线程"睡眠",同时占用的cpu资源
3. Object#wait 阻塞当前线程，不释放占用的资源，前提是当前线程必需获取这个对象的锁
4. Thread#join 阻塞当前线程，基于`Object#wait`实现

* JDK常用并发安全集合类
 

1. `Collections#synchronizedCollection`,直接将一个集合的所有操作转化成同步操作，实现简单，基于`synchronized`关键字
2. `ConcurrentHashMap` 基于分段的思想，ConcurrentHashMap可以做到读取数据不加锁，并且其内部的结构可以让其在进行写操作的时候能够将锁的粒度保持地尽量地小，不用对整个ConcurrentHashMap加锁。
3. `CopyOnWriteArrayList` 顾名思义，在写的时候的复制一份旧的副本，新元素写在副本上，写完之后替换掉原来的数组，整个过程需要加锁保证原子性


* J.U.C api


1. `Lock`，锁api，除了常用的`lock`,`unlock`外，还提供了`tryLock`，用于判断可以是否获取锁，不会被阻塞
2. `ReentrantLock`, 可重入锁
3. `ReentrantReadWriteLock` 可重入读写锁
4. `LockSupport` 锁的操作类，静态操作
5. `CountDownLatch`,阻塞主线程，等待子线程完成
6. `CyclicBarrier`，阻塞当前线程，直到所有线程都执行完成，可以复用
7. `Semaphare`信号量，count累计小于0时阻塞
8. `AtomicXX`，基本类型的原子操作

### 4)Spring 和 ORM 等框架
* Spring

Spring最初是作为Java平台的IOC实现，后面整合了一些JavaEE的规范，并衍生出其他子项目,比如：
1. Spring JDBC
2. Spring MVC
3. Spring Integration
目前，Spring基本覆盖了整个JavaEE的开发领域
  
* Hibernate和MyBatis 

Hibernate是一个开放源代码的对象关系映射框架，它对JDBC进行了非常轻量级的对象封装，使得Java程序员可以随心所欲的使用对象编程思维来操纵数据库。

MyBatis也是一种ORM的框架，目的在于简化JDBC操作，但与Hibernate不同的是，MyBatis并不关注实体与实体之间的关联，因此MyBatis执行的sql语句往往需要用户自己编写。


### 5)MySQL 数据库和 SQL
* MySQL常见数据库引擎
  
1. InnoDB  InnoDB是事务型数据库的首选引擎，支持事务安全表（ACID），支持行锁定和外键，上图也看到了，InnoDB是默认的MySQL引擎。
2. MyISAM MyISAM基于ISAM存储引擎，并对其进行扩展。它是在Web、数据仓储和其他应用环境下最常使用的存储引擎之一。MyISAM拥有较高的插入、查询速度，但不支持事物。
3. MEMORY MEMORY存储引擎将表中的数据存储到内存中，未查询和引用其他表数据提供快速访问。
4. Archive 专门用于数据归档使用

* MySQL锁

1. 共享锁（读锁）其他事务可以读，但不能写。
2. 排他锁（写锁）其他事务不能读取，也不能写。 
3. 表级锁：开销小，加锁快；不会出现死锁；锁定粒度大，发生锁冲突的概率最高，并发度最低。
4. 行级锁：开销大，加锁慢；会出现死锁；锁定粒度最小，发生锁冲突的概率最低，并发度也最高。


* MySQL事务隔离级别

4种事务隔离级别
1. Read uncommitted
读未提交，顾名思义，就是一个事务可以读取另一个未提交事务的数据。

2. Read committed
读提交，顾名思义，就是一个事务要等另一个事务提交后才能读取数据。
   
3. Repeatable read
重复读，就是在开始读取数据（事务开启）时，不再允许修改操作。MySQL默认支持的隔离级别
   
4. Serializable
所有事务以串行的方式执行，安全性最好，同时性能最低

### 6) 分库分表
* 垂直分表

将一个表按照字段分成多表，每个表存储其中一部分字段。性能提升主要集中在热门数据的操作效率上，而且磁盘争用情况减少
* 垂直分库

按照业务将表进行分类，分布到不同的数据库上面，每个库可以放在不同的服务器上，它的核心理念是专库专用。
  
**垂直分库分表主要解决的是热点数据的操作效率问题，但是并没有解决单表数据量过大的问题**

* 水平分库

水平分库是把同一个表的数据按一定规则拆到不同的数据库中，每个库可以放在不同的服务器上。

* 水平分表

水平分表是在同一个数据库内，把同一个表的数据按一定规则拆到多个表中。

**分库分表带来的问题**
1. 事务一致性问题. 由于分库分表把数据分布在不同库甚至不同服务器，不可避免会带来分布式事务问题。
2. 跨节点关联查询 分库分表后，不同数据可能在不同服务器，因此需要对关联查询进行拆分和改造

### 7)RPC 和微服务
RPC，远程过程调用。是一个请求响应模型。客户端发起请求，服务器返回响应（类似于Http的工作方式）。主要实现两个进程之间的功能通信

微服务是一种更细粒度的SOA。平台随着业务的发展从 All in One 环境就可以满足业务需求（对Java来说，可能只是一两个war包就解决了）。
发展到需要拆分多个应用，并且采用MVC的方式分离前后端，加快开发效率；在发展到服务越来越多，不得不将一些核心或共用的服务拆分出来，当拆分的足够精细，这时候就可以认为是微服务了。
### 8) 分布式缓存

### 9) 分布式消息队列
消息队列类似于生产者-消费者模型。消息队列主要解决了应用耦合、异步处理、流量削锋等问题。
* 点对点模式

每个消息有且只有一个接收者。发送者和接收者间没有依赖性，发送者发送消息之后，不管有没有接收者在运行，都不会影响到发送者下次发送消息。接收者在成功接收消息之后需向队列应答成功
  
* 发布订阅模式

每个消息可以有多个订阅者。发布者和订阅者之间有时间上的依赖性。针对某个主题（Topic）的订阅者，它必须创建一个订阅者之后，才能消费发布者的消息。为了消费消息，订阅者需要提前订阅该角色主题，并保持在线运行

* 