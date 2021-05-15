## GC 总结

#### Serial GC

​	串行GC对年轻代使用 mark-copy（标记-复制） 算法，对老年代使用 mark-sweep-compact（标记清除-整理） 算法。都是单线程执行，并且在整个执行的过程中都会触发`STW`。在多核CPU环境中，这种单核操作的GC执行效率并不高，因此不推荐在多核环境中使用。



#### Parallel GC

##### ParNew

`ParNew`收集器适用对年轻代的回收，采用mark-copy（标记-复制） 算法。可以理解成`Serial GC`的多线程版本（年轻代），因为在GC过程中开启多线程执行，因此`STW`的时长要短于`Serial GC`(多核CPU环境下)

同样一段代码，在多核CPU环境下分别使用`Serial GC`和`ParNew`的GC日志输出如下：

```
## Serial GC输出日志
CommandLine flags: -XX:InitialHeapSize=1073741824 -XX:MaxHeapSize=1073741824 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseSerialGC 
2021-05-09T18:12:13.986+0800: 0.210: [GC (Allocation Failure) 2021-05-09T18:12:13.986+0800: 0.210: [DefNew: 279476K->34944K(314560K), 0.0249865 secs] 279476K->81444K(1013632K), 0.0251449 secs] [Times: user=0.00 sys=0.01, real=0.02 secs] 
2021-05-09T18:12:14.051+0800: 0.275: [GC (Allocation Failure) 2021-05-09T18:12:14.051+0800: 0.275: [DefNew: 314560K->34943K(314560K), 0.0316838 secs] 361060K->157719K(1013632K), 0.0317388 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-05-09T18:12:14.119+0800: 0.343: [GC (Allocation Failure) 2021-05-09T18:12:14.119+0800: 0.343: [DefNew: 314559K->34942K(314560K), 0.0250324 secs] 437335K->234182K(1013632K), 0.0250934 secs] [Times: user=0.01 sys=0.02, real=0.03 secs] 
2021-05-09T18:12:14.181+0800: 0.405: [GC (Allocation Failure) 2021-05-09T18:12:14.181+0800: 0.405: [DefNew: 314370K->34944K(314560K), 0.0276177 secs] 513609K->319293K(1013632K), 0.0276648 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2021-05-09T18:12:14.249+0800: 0.473: [GC (Allocation Failure) 2021-05-09T18:12:14.249+0800: 0.473: [DefNew: 314560K->34943K(314560K), 0.0272654 secs] 598909K->400606K(1013632K), 0.0273236 secs] [Times: user=0.00 sys=0.02, real=0.03 secs] 
2021-05-09T18:12:14.313+0800: 0.537: [GC (Allocation Failure) 2021-05-09T18:12:14.313+0800: 0.537: [DefNew: 314559K->34943K(314560K), 0.0249003 secs] 680222K->476714K(1013632K), 0.0249509 secs] [Times: user=0.02 sys=0.00, real=0.03 secs] 
2021-05-09T18:12:14.371+0800: 0.599: [GC (Allocation Failure) 2021-05-09T18:12:14.371+0800: 0.599: [DefNew: 314559K->34943K(314560K), 0.0270586 secs] 756330K->560864K(1013632K), 0.0271174 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2021-05-09T18:12:14.434+0800: 0.664: [GC (Allocation Failure) 2021-05-09T18:12:14.434+0800: 0.664: [DefNew: 314559K->34943K(314560K), 0.0261298 secs] 840480K->640640K(1013632K), 0.0261792 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-05-09T18:12:14.497+0800: 0.728: [GC (Allocation Failure) 2021-05-09T18:12:14.497+0800: 0.728: [DefNew: 314559K->314559K(314560K), 0.0000113 secs]2021-05-09T18:12:14.497+0800: 0.728: [Tenured: 605697K->371883K(699072K), 0.0374033 secs] 920256K->371883K(1013632K), [Metaspace: 3134K->3134K(1056768K)], 0.0374912 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
```

```
## ParNew GC输出日志
CommandLine flags: -XX:InitialHeapSize=1073741824 -XX:MaxHeapSize=1073741824 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParNewGC 
2021-05-09T18:27:04.518+0800: 0.229: [GC (Allocation Failure) 2021-05-09T18:27:04.518+0800: 0.229: [ParNew: 279197K->34944K(314560K), 0.0118791 secs] 279197K->78402K(1013632K), 0.0120168 secs] [Times: user=0.02 sys=0.14, real=0.02 secs] 
2021-05-09T18:27:04.566+0800: 0.278: [GC (Allocation Failure) 2021-05-09T18:27:04.566+0800: 0.278: [ParNew: 314560K->34939K(314560K), 0.0167860 secs] 358018K->162602K(1013632K), 0.0168339 secs] [Times: user=0.01 sys=0.14, real=0.02 secs] 
2021-05-09T18:27:04.629+0800: 0.334: [GC (Allocation Failure) 2021-05-09T18:27:04.629+0800: 0.334: [ParNew: 314555K->34943K(314560K), 0.0253576 secs] 442218K->236059K(1013632K), 0.0254109 secs] [Times: user=0.14 sys=0.00, real=0.02 secs] 
2021-05-09T18:27:04.691+0800: 0.397: [GC (Allocation Failure) 2021-05-09T18:27:04.691+0800: 0.397: [ParNew: 314559K->34944K(314560K), 0.0315339 secs] 515675K->323420K(1013632K), 0.0315852 secs] [Times: user=0.26 sys=0.02, real=0.03 secs] 
2021-05-09T18:27:04.754+0800: 0.469: [GC (Allocation Failure) 2021-05-09T18:27:04.754+0800: 0.469: [ParNew: 314560K->34942K(314560K), 0.0261708 secs] 603036K->394989K(1013632K), 0.0262236 secs] [Times: user=0.28 sys=0.02, real=0.03 secs] 
2021-05-09T18:27:04.816+0800: 0.531: [GC (Allocation Failure) 2021-05-09T18:27:04.816+0800: 0.531: [ParNew: 314558K->34942K(314560K), 0.0314388 secs] 674605K->481493K(1013632K), 0.0314775 secs] [Times: user=0.30 sys=0.02, real=0.03 secs] 
2021-05-09T18:27:04.899+0800: 0.602: [GC (Allocation Failure) 2021-05-09T18:27:04.899+0800: 0.602: [ParNew: 314558K->34943K(314560K), 0.0293246 secs] 761109K->563242K(1013632K), 0.0293692 secs] [Times: user=0.31 sys=0.00, real=0.03 secs] 
2021-05-09T18:27:04.957+0800: 0.671: [GC (Allocation Failure) 2021-05-09T18:27:04.957+0800: 0.671: [ParNew: 314559K->34942K(314560K), 0.0298057 secs] 842858K->645288K(1013632K), 0.0298536 secs] [Times: user=0.24 sys=0.03, real=0.03 secs] 
2021-05-09T18:27:05.025+0800: 0.740: [GC (Allocation Failure) 2021-05-09T18:27:05.025+0800: 0.740: [ParNew: 314558K->314558K(314560K), 0.0000119 secs]2021-05-09T18:27:05.025+0800: 0.740: [Tenured: 610345K->369280K(699072K), 0.0361702 secs] 924904K->369280K(1013632K), [Metaspace: 3134K->3134K(1056768K)], 0.0362540 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 

```

可以明显看到，在回收相同容量的内存时，`ParNew`的效率要高于`Serial GC`



##### Parallel Scavenge

与`ParNew`类似，`Parallel Scavenge`也是适用于年轻代的垃圾回收，同时也使用mark-copy（标记-复制） 算法。但是与`ParNew`不同点在于，`Parallel Scavenge`的目标是达到一个可控的吞吐量。

为了使吞吐量可控，`Parallel Scavenge`提供了两个参数用于调整GC回收策略

```
-XX:MaxGCPauseMillis=50 ##控制最大垃圾收集停顿时间的，默认值为200，即最大GC暂停时间不超过200ms
-XX:GCTimeRatio ##垃圾收集时间占总时间的比率（0-100）
```

设置好参数后，在GC回收过程中，`Parallel Scavenge`会逐步调整回收策略以达到设置的目标

下面是运行`Parallel Scavenge`的gc输出日志

```
CommandLine flags: -XX:InitialHeapSize=1073741824 -XX:MaxHeapSize=1073741824 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC 
2021-05-09T18:18:50.985+0800: 0.245: [GC (Allocation Failure) [PSYoungGen: 262144K->43519K(305664K)] 262144K->76551K(1005056K), 0.0209111 secs] [Times: user=0.00 sys=0.00, real=0.02 secs] 
2021-05-09T18:18:51.050+0800: 0.310: [GC (Allocation Failure) [PSYoungGen: 305663K->43518K(305664K)] 338695K->146217K(1005056K), 0.0175095 secs] [Times: user=0.02 sys=0.11, real=0.02 secs] 
2021-05-09T18:18:51.098+0800: 0.370: [GC (Allocation Failure) [PSYoungGen: 305662K->43519K(305664K)] 408361K->218367K(1005056K), 0.0146373 secs] [Times: user=0.13 sys=0.00, real=0.02 secs] 
2021-05-09T18:18:51.161+0800: 0.424: [GC (Allocation Failure) [PSYoungGen: 305663K->43515K(305664K)] 480511K->290814K(1005056K), 0.0147943 secs] [Times: user=0.03 sys=0.13, real=0.02 secs] 
2021-05-09T18:18:51.207+0800: 0.475: [GC (Allocation Failure) [PSYoungGen: 305659K->43516K(305664K)] 552958K->362852K(1005056K), 0.0147146 secs] [Times: user=0.00 sys=0.16, real=0.02 secs] 
2021-05-09T18:18:51.254+0800: 0.525: [GC (Allocation Failure) [PSYoungGen: 305660K->43517K(160256K)] 624996K->436407K(859648K), 0.0152073 secs] [Times: user=0.27 sys=0.00, real=0.03 secs] 
2021-05-09T18:18:51.296+0800: 0.555: [GC (Allocation Failure) [PSYoungGen: 160055K->64405K(232960K)] 552945K->465368K(932352K), 0.0062464 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:18:51.312+0800: 0.578: [GC (Allocation Failure) [PSYoungGen: 181141K->86152K(232960K)] 582104K->494094K(932352K), 0.0080698 secs] [Times: user=0.11 sys=0.03, real=0.01 secs] 
2021-05-09T18:18:51.343+0800: 0.603: [GC (Allocation Failure) [PSYoungGen: 202888K->101981K(232960K)] 610830K->527932K(932352K), 0.0106143 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:18:51.359+0800: 0.629: [GC (Allocation Failure) [PSYoungGen: 218717K->78711K(232960K)] 644668K->558984K(932352K), 0.0122387 secs] [Times: user=0.02 sys=0.13, real=0.02 secs] 
2021-05-09T18:18:51.401+0800: 0.658: [GC (Allocation Failure) [PSYoungGen: 195447K->38133K(232960K)] 675720K->587448K(932352K), 0.0120410 secs] [Times: user=0.05 sys=0.11, real=0.01 secs] 
2021-05-09T18:18:51.426+0800: 0.685: [GC (Allocation Failure) [PSYoungGen: 154869K->44088K(232960K)] 704184K->626711K(932352K), 0.0080417 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:18:51.441+0800: 0.709: [GC (Allocation Failure) [PSYoungGen: 160782K->37864K(232960K)] 743405K->659883K(932352K), 0.0081505 secs] [Times: user=0.08 sys=0.06, real=0.02 secs] 
2021-05-09T18:18:51.457+0800: 0.717: [Full GC (Ergonomics) [PSYoungGen: 37864K->0K(232960K)] [ParOldGen: 622019K->322976K(699392K)] 659883K->322976K(932352K), [Metaspace: 3134K->3134K(1056768K)], 0.0344838 secs] [Times: user=0.27 sys=0.02, real=0.03 secs] 
```

可以看到，相比于上面的`ParNew`，`Parallel Scavenge`每次GC执行时间要明显短于`ParNew`，但是伴随的副作用就是GC的次数明显增多，以及每次GC回收的内存数量变少。

其实可以理解成，为了缩短每次的GC时长，`Parallel Scavenge`会降低GC开始执行的指标。***与其在每天放学回家完成所有作业，不如在老师开始布置作业的时侯就在学校里写***



##### Parallel Old

`Parallel Old`是`Parallel Scavenge`收集器的老年代版本，用于老年代的垃圾回收，但与`Parallel Scavenge`不同的是，它使用的是“标记-整理算法”。



#### CMS GC

`CMS GC`用于回收老年代，对老年代主要使用 并发 mark-sweep (标记-清除)算法 。

`CMS GC`相比于上面的`Serial GC`和`Parallel GC`最大的不同处在于，`CMS GC`的`STW`并不会贯穿GC的整个阶段，这样会降低系统因为GC产生的延迟。但是这样GC占用CPU资源的时间就会相对变长，其吞吐量会稍差于`Parallel GC`

下面是`CMS GC`的GC日志输出

```
CommandLine flags: -XX:InitialHeapSize=1073741824 -XX:MaxHeapSize=1073741824 -XX:MaxNewSize=357916672 -XX:MaxTenuringThreshold=6 -XX:NewSize=357916672 -XX:OldPLABSize=16 -XX:OldSize=715825152 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:-UseLargePagesIndividualAllocation -XX:+UseParNewGC
2021-05-09T18:32:32.950+0800: 0.890: [GC (CMS Initial Mark) [1 CMS-initial-mark: 400134K(699072K)] 435235K(1013632K), 0.0002323 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:32:32.950+0800: 0.890: [CMS-concurrent-mark-start]
2021-05-09T18:32:32.951+0800: 0.891: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.06 sys=0.00, real=0.00 secs] 
2021-05-09T18:32:32.951+0800: 0.891: [CMS-concurrent-preclean-start]
2021-05-09T18:32:32.951+0800: 0.892: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:32:32.951+0800: 0.892: [CMS-concurrent-abortable-preclean-start]
2021-05-09T18:32:32.983+0800: 0.929: [GC (Allocation Failure) 2021-05-09T18:32:32.983+0800: 0.929: [ParNew: 314536K->34942K(314560K), 0.0154893 secs] 714670K->516816K(1013632K), 0.0155381 secs] [Times: user=0.16 sys=0.00, real=0.02 secs] 
2021-05-09T18:32:33.036+0800: 0.980: [GC (Allocation Failure) 2021-05-09T18:32:33.036+0800: 0.980: [ParNew: 314558K->34943K(314560K), 0.0147737 secs] 796432K->603681K(1013632K), 0.0148369 secs] [Times: user=0.16 sys=0.00, real=0.02 secs] 
2021-05-09T18:32:33.083+0800: 1.031: [GC (Allocation Failure) 2021-05-09T18:32:33.083+0800: 1.031: [ParNew: 314559K->34943K(314560K), 0.0246508 secs] 883297K->690185K(1013632K), 0.0247141 secs] [Times: user=0.30 sys=0.02, real=0.03 secs] 
2021-05-09T18:32:33.115+0800: 1.056: [CMS-concurrent-abortable-preclean: 0.005/0.165 secs] [Times: user=0.70 sys=0.02, real=0.16 secs] 
2021-05-09T18:32:33.115+0800: 1.057: [GC (CMS Final Remark) [YG occupancy: 49536 K (314560 K)]2021-05-09T18:32:33.115+0800: 1.057: [Rescan (parallel) , 0.0003777 secs]2021-05-09T18:32:33.115+0800: 1.057: [weak refs processing, 0.0000301 secs]2021-05-09T18:32:33.115+0800: 1.057: [class unloading, 0.0002088 secs]2021-05-09T18:32:33.115+0800: 1.057: [scrub symbol table, 0.0003695 secs]2021-05-09T18:32:33.115+0800: 1.058: [scrub string table, 0.0000868 secs][1 CMS-remark: 655242K(699072K)] 704778K(1013632K), 0.0011214 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:32:33.115+0800: 1.058: [CMS-concurrent-sweep-start]
2021-05-09T18:32:33.115+0800: 1.059: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-09T18:32:33.115+0800: 1.059: [CMS-concurrent-reset-start]
2021-05-09T18:32:33.115+0800: 1.059: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

整个`CMS GC`中，大致可以划分成7个阶段

- Initial Mark：初始化标记，标记所有的根对象（需要STW）
- Concurrent Mark：并发标记，从上一阶段的根对象触发，标记所有存活对象，此阶段不需要STW，因此可以和用户线程同时运行
- Concurrent Preclean： 并发预清理，利用`Card Marking`的方式标记在上一阶段程序并发导致对象引用关系变化的区域
- Concurrent Abortable Preclean： 此阶段也不停止应用线程。主要清理上一个阶段用于标记的`Card`，同时也在判断下一个阶段的执行时机
- Final Remark：此阶段会触发`STW`,完成老年代中所有存活对象的标记
- :Concurrent Sweep：并发清除上一个阶段所以未标记的对象，不需要STW
- Concurrent Reset：并发重置，重置CMS算法的一些内部数据，不需要STW



#### G1 GC

在传统的GC收集器(`Serial`,`Parallel`,`CMS`)无一不例外都把heap分成两个空间：年轻代和老年代。

在G1中堆被分成一块块大小相等的heap region，一般有2000多块，这些region在逻辑上是连续的。每块region都会被打唯一的分代标志(eden,survivor,old)。在逻辑上，eden regions构成Eden空间，survivor regions构成Survivor空间，old regions构成了old 空间。

通过命令行参数`-XX:NewRatio=n`来配置新生代与老年代的比例，默认为2，即比例为2:1；`-XX:SurvivorRatio=n`则可以配置Eden与Survivor的比例，默认为8。

GC时G1的运行方式与CMS方式类似，会有一个全局并发标记(concurrent global marking phase)的过程，去确定堆里对象的的存活情况。并发标记完成之后，G1知道哪些regions空闲空间多(可回收对象多),优先回收这些空的regions，释放出大量的空闲空间。这是为什么这种垃圾回收方式叫G1的原因(Garbage-First)。



#### Appendix

常用GC组合

> ### -XX:+UseSerialGC
>
> Serial + Serial Old
>
> ### -XX:+UseParNewGC (Deprecated)
>
> Parallel +  Serial Old
>
> ### -XX:+UseParallelGC
>
> ### -XX:+UseParallelOldGC
>
> Parallel Scavenge + Parallel Old
>
> ### -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
>
> Parallel (ParNew) + CMS
>
> ### -XX:+UseG1GC
>
> G1



Java8默认GC组合

> ### -XX:+UseParallelGC
>
> Parallel Scavenge + Parallel Old