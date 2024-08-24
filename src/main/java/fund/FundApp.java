package fund;

import fund.bean.FundBean;
import fund.task.FundDataGetRunnable;
import fund.utils.FundUtil;
import utils.FileUtil;
import utils.LogUtil;
import utils.ReflectUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static fund.constant.FundConstant.FILE_ABSOLUTE_PATH;

/**
 * 基金数据爬取整理APP
 *
 * @author cjl
 * @since 2024/7/4.
 */
public class FundApp {
    /**
     * 入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // test();
        work();
    }

    /**
     * 全量爬取
     */
    private static void work() {

        long startTime = TimeUtil.now();

        // 1、构建一个按照参数创建的线程池
        int corePoolSize = 4; // 核心线程数
        int maximumPoolSize = 4; // 最大线程数
        long keepAliveTime = 10L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100); // 任务队列
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                // 拒绝策略：用提交任务的线程来执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 2、初始化文件路径和内容
        // String todayDate = TimeUtil.YYYY_MM_DD_SDF.format(new Date());
        String todayDate = "2024-08-23";
        String path = String.format(FILE_ABSOLUTE_PATH, "base-" + todayDate);
        // 防止重复运行报错
        FileUtil.deleteFile(path);
        FileUtil.writeStringToFile(path, ReflectUtil.getAllDescriptionFieldsDesc(FundBean.class), true);

        // 3、获取全量基金id，往线程池里提交查询任务
        Set<String> allIds = FundUtil.getAllFundIdsFromWeb();
        for (String id : allIds) {
            FundDataGetRunnable task = new FundDataGetRunnable(todayDate, path, id);
            threadPoolExecutor.execute(task);
        }

        // 4、无限等待任务完成，然后关闭线程池释放资源
        threadPoolExecutor.shutdown(); // 不再接受新任务，但会继续执行队列中的任务
        try {
            // 等待所有任务完成，或者直到超时（在这个例子中，基本上是无限期等待）
            if (!threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                // 理论上，如果线程池没有正确关闭（即还有任务在运行），这里会被执行
                // 但由于我们设置了Long.MAX_VALUE作为超时时间，所以这里实际上不太可能被执行到
                // 除非线程池被外部因素（如系统关闭）干扰
            }
        } catch (InterruptedException e) {
            // 当前线程在等待过程中被中断
            // 处理中断，例如记录日志、重新设置中断状态等
            // 注意：通常不建议在捕获到InterruptedException后直接调用shutdownNow()，因为这可能会留下未完成的任务
            Thread.currentThread().interrupt(); // 重新设置中断状态
        }

        LogUtil.info("!!!所有任务完成，耗时：{}(ms)", TimeUtil.now() - startTime);
    }

    private static void test() {
        String testId = "006150";
        String todayDate = TimeUtil.YYYY_MM_DD_SDF.format(new Date());
        String path = String.format(FILE_ABSOLUTE_PATH, "test-" + todayDate);
        FileUtil.deleteFile(path);
        FileUtil.writeStringToFile(path, ReflectUtil.getAllDescriptionFieldsDesc(FundBean.class), true);
        FundDataGetRunnable task = new FundDataGetRunnable(todayDate, path, testId);
        Thread thread = new Thread(task);
        thread.start();
    }
}
