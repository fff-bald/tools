package funddata;

import funddata.bean.FundDataBean;
import funddata.task.FundDataGetRunnable;
import utils.FileUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static funddata.constant.FundDataConstant.FILE_ABSOLUTE_PATH;

/**
 * 基金数据爬取整理APP
 *
 * @author cjl
 * @since 2024/7/4.
 */
public class FundDataCollationApp {
    /**
     * 入口方法
     *
     * @param args
     */
    public static void main(String[] args) {

        // 构建一个按照参数创建的线程池
        int corePoolSize = 1; // 核心线程数
        int maximumPoolSize = 3; // 最大线程数
        long keepAliveTime = 10L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000); // 任务队列
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );


        String path = String.format(FILE_ABSOLUTE_PATH, "base-" + TimeUtil.YYYY_MM_DD_SDF.format(new Date()));
        Set<String> allIds = FundDataCollationUtil.getAllFundIdsFromWeb();
        FileUtil.writeStringToFile(path, FundDataCollationUtil.getAllFieldsExceptList(FundDataBean.class), true);

        for (String id : allIds) {
            FundDataGetRunnable task = new FundDataGetRunnable(id, false);
            threadPoolExecutor.execute(task);
        }

        // 关闭线程池
        threadPoolExecutor.shutdown();
    }
}
