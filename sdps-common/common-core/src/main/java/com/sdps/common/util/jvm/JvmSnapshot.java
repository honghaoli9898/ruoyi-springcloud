package com.sdps.common.util.jvm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by lubiao on 2018/4/2.
 */
@Getter
@Setter
@ToString
public class JvmSnapshot {
    private long startTime;

    private long youngUsed;//新生代已用内存

    private long youngMax;//新生代最大内存

    private long oldUsed;//老年代已用内存

    private long oldMax;//老年代最大内存

    private long youngCollectionCount;//新生代当前累积的垃圾回收次数

    private long oldCollectionCount;//老年代当前累积的垃圾回收次数

    private long youngCollectionTime;//新生代当前累积的垃圾回收时间

    private long oldCollectionTime;//老年代当前累积的垃圾回收时间

    private int currentThreadCount;//当前线程数

}
