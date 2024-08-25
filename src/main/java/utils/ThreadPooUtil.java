package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author cjl
 * @since 2024/8/26 0:07
 */
public class ThreadPooUtil {

    public static ThreadPoolExecutor createCommonPool() {
        int corePoolSize = 3; // 核心线程数
        int maximumPoolSize = 24; // 最大线程数
        long keepAliveTime = 10L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100); // 任务队列

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                // 拒绝策略：用提交任务的线程来执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
