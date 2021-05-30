package indi.kurok1;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * 作业练习
 * 详细实现
 * <ol>
 *     <li>使用阻塞队列实现 {@link HomeWork#runWithBlockingQueue()}</li>
 *     <li>使用wait的方式实现 {@link HomeWork#runWithWait()}</li>
 *     <li>使用join的方式实现 {@link HomeWork#runWithJoin()}</li>
 *     <li>使用interrupt的方式实现 {@link HomeWork#runWithInterrupt()}</li>
 *     <li>使用Future和Executor的方式来实现 {@link HomeWork#runWithFuture()}</li>
 *     <li>使用ForkJoinPool的方式来实现 {@link HomeWork#runWithForkJoinPool()}</li>
 *     <li>使用CompleteFuture的方式来实现 {@link HomeWork#runWithCompleteFuture()}</li>
 *     <li>使用Condition的方式来实现 {@link HomeWork#runWithCondition()}</li>
 *     <li>使用ReadWriteLock的方式来实现 {@link HomeWork#runWithReadWriteLock()}</li>
 *     <li>使用CountDownLatch的方式来实现 {@link HomeWork#runWithCountDownLatch()}</li>
 *     <li>使用Semaphore的方式来实现 {@link HomeWork#runWithSemaphore()}</li>
 *     <li>使用CyclicBarrier的方式来实现 {@link HomeWork#runWithCyclicBarrier()}</li>
 *     <li>使用Park的方式来实现 {@link HomeWork#runWithPark()}</li>
 * </ol>
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.05.29
 */
public class HomeWork {

    private static int sum() {
        //先睡眠0.5s,模拟延迟
        System.out.printf("线程[%s]正在执行计算任务: ", Thread.currentThread().getName());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fibo(15);
    }

    private static int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }


    public static void main(String[] args) {
        System.out.printf("当前主线程[%s]\n", Thread.currentThread().getName());
        for (int i = 0; i < 10; i++) {
            runWithPark();
        }

    }

    /**
     * todo 以下是具体实现
     */

    /**
     * 使用wait和AtomicInteger的方式来进行实现
     */
    private static void runWithJoin() {
        long start=System.currentTimeMillis();
        final AtomicInteger num = new AtomicInteger(0);
        Thread thread = new Thread() {
            @Override
            public void run() {
                num.set(sum());
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("异步计算结果为："+num.get());

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用wait的方式来实现
     */
    private static void runWithWait() {
        long start=System.currentTimeMillis();
        final AtomicInteger num = new AtomicInteger(0);
        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (num) {
                    num.set(sum());
                    num.notifyAll();
                }
            }
        };
        thread.start();
        synchronized (num) {
            if (num.get() == 0) {
                try {
                    num.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("异步计算结果为："+num.get());

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 利用InterruptedException中断等待完成通信
     */
    private static void runWithInterrupt() {
        long start=System.currentTimeMillis();
        final AtomicInteger num = new AtomicInteger(0);
        Thread thread = new InterruptThread(Thread.currentThread(), num);
        thread.start();
        try {
            Thread.sleep(Integer.MAX_VALUE);//无休止睡眠，等待中断
        } catch (InterruptedException e) {
            System.out.println("异步计算结果为："+num.get());
        }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static class InterruptThread extends Thread {
        private final Thread mainThread;
        private final AtomicInteger num;

        public InterruptThread(Thread mainThread, AtomicInteger num) {
            super();
            this.mainThread = mainThread;
            this.num = num;
        }

        @Override
        public void run() {
            num.set(sum());
            mainThread.interrupt();
        }
    }

    /**
     * 使用阻塞队列的方式来实现
     */
    private static void runWithBlockingQueue() {
        long start=System.currentTimeMillis();
        final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                blockingQueue.add(sum());
            }
        };
        thread.start();

        try {
            System.out.println("异步计算结果为："+blockingQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }


    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * 使用Future和Executor的方式来实现
     */
    private static void runWithFuture() {
        long start=System.currentTimeMillis();
        Future<Integer> future = executorService.submit(HomeWork::sum);

       while (!future.isDone()) {
           try {
               System.out.println("异步计算结果为：" + future.get());
               break;
           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (ExecutionException e) {
               e.printStackTrace();
           }
       }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用ForkJoinPool的方式实现
     */
    private static void runWithForkJoinPool() {
        long start=System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        RecursiveTask<Integer> task = new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                return sum();
            }
        };

        System.out.println("异步计算结果为：" + pool.invoke(task));

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用CompleteFuture实现
     */
    private static void runWithCompleteFuture() {
        long start=System.currentTimeMillis();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(HomeWork::sum, executorService);

        try {
            System.out.println("异步计算结果为：" + completableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用Condition实现
     */
    private static void runWithCondition() {
        long start=System.currentTimeMillis();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        final AtomicInteger num = new AtomicInteger(0);
        Thread thread = new Thread() {
            @Override
            public void run() {
                lock.lock();
                num.set(sum());
                condition.signalAll();
                lock.unlock();
            }
        };
        thread.start();

        lock.lock();
        if (num.get() == 0) {
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("异步计算结果为："+num.get());
        lock.unlock();

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用ReadWriteLock实现
     */
    private static void runWithReadWriteLock() {
        long start=System.currentTimeMillis();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();
        final AtomicInteger num = new AtomicInteger(0);
        Thread thread = new Thread() {
            @Override
            public void run() {
                writeLock.lock();
                num.set(sum());
                writeLock.unlock();
            }
        };
        thread.start();
        try {
            Thread.sleep(100);//先放弃资源一小会
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readLock.lock();
        System.out.println("异步计算结果为："+num.get());
        readLock.unlock();
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用CountDownLatch实现
     */
    private static void runWithCountDownLatch() {
        long start=System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicInteger num = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                num.set(sum());
                countDownLatch.countDown();
            }
        }.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("异步计算结果为："+num.get());

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用Semaphore实现
     */
    private static void runWithSemaphore () {
        long start=System.currentTimeMillis();
        final Semaphore semaphore = new Semaphore(1);
        final AtomicInteger num = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();//先控制资源，防止主线程读取
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                num.set(sum());
                semaphore.release();//计算完成后释放资源
            }
        }.start();
        try {
            while (num.get() == 0) {
                Thread.sleep(50);
                semaphore.acquire();//不断试获取资源，成功了则代表子线程计算成功
                semaphore.release();
            }
            System.out.println("异步计算结果为："+num.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用CyclicBarrier实现
     */
    private static void runWithCyclicBarrier() {
        long start=System.currentTimeMillis();
        final CyclicBarrier barrier = new CyclicBarrier(2);//算上主线程是2个
        final AtomicInteger num = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                num.set(sum());
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        try {
            barrier.await();//主线程先行等待
            System.out.println("异步计算结果为："+num.get());
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    /**
     * 使用Park的方式实现
     */
    private static void runWithPark() {
        long start=System.currentTimeMillis();
        final AtomicInteger num = new AtomicInteger(0);
        ParkThread thread = new ParkThread(Thread.currentThread(), num);
        thread.start();
        LockSupport.park(num);
        System.out.println("异步计算结果为："+num.get());

        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static class ParkThread extends Thread {
        private final Thread mainThread;
        private final AtomicInteger num;

        public ParkThread(Thread mainThread, AtomicInteger num) {
            this.mainThread = mainThread;
            this.num = num;
        }

        @Override
        public void run() {
            num.set(sum());
            LockSupport.unpark(mainThread);
        }
    }
}
