## 作业说明

不同GC的总结: [GC总结](src/main/java/resources/GC Summary.md)

### HttpServer分析

#### 单线程阻塞io ([实现代码](src/main/java/indi/kurok1/http/server/SingleThreadHttpServer.java))

在windows平台（cpu: AMD3600x, ram:16g）上压测结果如下

```
##-Xmx1g -Xms1g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
E:\>sb -u http://127.0.0.1:8081 -c 40 -N 30
Starting at 2021/5/16 0:31:50
[Press C to stop the test]
35416   (RPS: 1058.4)
---------------Finished!----------------
Finished at 2021/5/16 0:32:24 (took 00:00:33.6358203)
Status 303:    35424

RPS: 1134.7 (requests/second)
Max: 183ms
Min: 0ms
Avg: 30.6ms

  50%   below 30ms
  60%   below 42ms
  70%   below 50ms
  80%   below 58ms
  90%   below 69ms
  95%   below 79ms
  98%   below 90ms
  99%   below 98ms
99.9%   below 123ms

```



#### 多线程阻塞io ([实现代码](src/main/java/indi/kurok1/http/server/NewThreadHttpServer.java))

每次请求时都创建一个新的线程处理

在windows平台（cpu: AMD3600x, ram:16g）上压测结果如下

```
##-Xmx1g -Xms1g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
E:\>sb -u http://127.0.0.1:8082 -c 40 -N 30
Starting at 2021/5/16 0:36:16
[Press C to stop the test]
46565   (RPS: 1395.5)
---------------Finished!----------------
Finished at 2021/5/16 0:36:50 (took 00:00:33.4152593)
Status 303:    46565

RPS: 1499.2 (requests/second)
Max: 151ms
Min: 0ms
Avg: 20.5ms

  50%   below 5ms
  60%   below 8ms
  70%   below 34ms
  80%   below 48ms
  90%   below 61ms
  95%   below 71ms
  98%   below 84ms
  99%   below 92ms
99.9%   below 115ms
```

总体来说性能要好于单线程，但是观察GC日志可以发现压测过程中产生多次GC（大约有395次young gc，每次GC的STW时间稳定在0.001s左右），在一定程度上影响了压测性能



#### 线程池阻塞io ([实现代码](src/main/java/indi/kurok1/http/server/ThreadPoolHttpServer.java))

在上一个实现，因为线程池的生产是无限的，因此产生的垃圾内存也是无限增长的，造成了多次GC，因此考虑使用线程池的方式管理线程

```java
private final static ExecutorService executorService = Executors.newFixedThreadPool(40);//固定大小为40
```

在windows平台（cpu: AMD3600x, ram:16g）上压测结果如下

```
##-Xmx1g -Xms1g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
E:\>sb -u http://127.0.0.1:8083 -c 40 -N 30
Starting at 2021/5/16 0:49:29
[Press C to stop the test]
35608   (RPS: 1065.7)
---------------Finished!----------------
Finished at 2021/5/16 0:50:02 (took 00:00:33.5811418)
Status 303:    35617

RPS: 1141.9 (requests/second)
Max: 167ms
Min: 0ms
Avg: 30.5ms

  50%   below 30ms
  60%   below 41ms
  70%   below 49ms
  80%   below 58ms
  90%   below 69ms
  95%   below 78ms
  98%   below 89ms
  99%   below 97ms
99.9%   below 123ms
```



这时候发现性能不升反降，推测原因可能为处理请求的过程太简单，导致其性能损耗远小于线程切换的代价。但是值得注意的时候使用线程池的过程中全程没有一次GC发生



### NIO ([实现代码](src/main/java/indi/kurok1/http/server/NioHttpServer.java))

这次采用nio的方式进行处理，对于每一个`SelectionKey`如果`isReabled`,采用`parallelStream`方式处理

在windows平台（cpu: AMD3600x, ram:16g）上压测结果如下

```
##-Xmx1g -Xms1g -XX:+PrintGCDateStamps -XX:+PrintGCDetails
E:\>sb -u http://127.0.0.1:8084 -c 40 -N 30
Starting at 2021/5/16 0:54:15
[Press C to stop the test]
137089  (RPS: 4086.5)
---------------Finished!----------------
Finished at 2021/5/16 0:54:49 (took 00:00:33.6728608)
Status 303:    137092

RPS: 4402.9 (requests/second)
Max: 97ms
Min: 0ms
Avg: 2.8ms

  50%   below 2ms
  60%   below 3ms
  70%   below 4ms
  80%   below 5ms
  90%   below 6ms
  95%   below 8ms
  98%   below 9ms
  99%   below 11ms
99.9%   below 23ms
```

NIO永远的神





### OkHttpDemo ([实现代码](src/main/java/indi/kurok1/http/client/OkHttpClientDemo.java))