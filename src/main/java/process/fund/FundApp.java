package process.fund;

import process.fund.bean.FundBean;
import process.fund.constant.FundConstant;
import process.fund.task.FundDataGetRunnable;
import process.fund.utils.FundDataBaseUtil;
import process.fund.utils.FundUtil;
import utils.*;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static process.fund.constant.FundConstant.CSV_FILE_ABSOLUTE_PATH;
import static process.fund.constant.FundConstant.EXCEL_FILE_ABSOLUTE_PATH;

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
        long startTime = TimeUtil.now();

        testCSV("");
        workExcel("2025-03-07");
        workCSV("");

        List<String> deleteIds = FundBeanFactory.getInstance().getInstanceContext().getDeleteIds();
        FundDataBaseUtil.clearFundDataInDataBase(deleteIds);

        CmdUtil.winCommand("shutdown -s -t 120");

        LogUtil.info("所有任务完成，耗时：{}(ms)", TimeUtil.now() - startTime);
    }

    /**
     * 全量爬取
     */
    private static void workExcel(String todayDate) {
        if (StringUtil.isBlank(todayDate)) {
            return;
        }

        long startTime = TimeUtil.now();

        // 1、构建一个按照参数创建的线程池
        ThreadPoolExecutor threadPoolExecutor = ThreadPooUtil.createCommonPool();

        // 2、获取全量基金id，构建上下文信息，往线程池里提交查询任务
        Set<String> allIds = FundUtil.getAllFundIdsFromWeb();
        FundHandlerContext.Builder builder = new FundHandlerContext.Builder();
        FundHandlerContext context = builder
                .setAllIdCount(allIds.size())
                .setDate(todayDate)
                .setPath(String.format(EXCEL_FILE_ABSOLUTE_PATH, "base-" + todayDate))
                .setWriteExcel(true)
                .build();
        FundBeanFactory.getInstance().updateInstanceContext(context);
        for (String id : allIds) {
            FundDataGetRunnable task = new FundDataGetRunnable(id);
            threadPoolExecutor.execute(task);
        }

        // 3、无限等待任务完成，然后关闭线程池释放资源
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

        // 4、根据数据生成Excel
        FundUtil.createExcel(context);

        // 5、发邮件
        if (FundConstant.NEED_EMAIL) {
            try {
                // 收件人信息
                String mailTo = FundConstant.RECEIVER_EMAIL_NAME;
                String subject = "自动发送 ----------> 文档：FundData" + context.getDate();
                String message = "workExcel方法执行耗时（s）：" + ((TimeUtil.now() - startTime) / 1000) +
                        "\n附件生成时间戳：" + DateUtil.getCurrentDateTime();
                // 附件文件路径
                String attachFile = context.getPath();

                EmailUtil.sendEmail(EmailUtil.EmailSendType.ONE_SIX_THREE
                        , mailTo, subject, message, attachFile);
                LogUtil.info("Email sent successfully.");
            } catch (MessagingException ex) {
                LogUtil.error("Could not send email, logReason: {}", ExceptionUtil.getStackTraceAsString(ex));
            }
        }

        LogUtil.info("workExcel方法执行完成，耗时：{}(ms)", TimeUtil.now() - startTime);
    }

    /**
     * 全量爬取，生成csv文件
     */
    private static void workCSV(String todayDate) {
        if (StringUtil.isBlank(todayDate)) {
            return;
        }

        // 1、构建一个按照参数创建的线程池
        ThreadPoolExecutor threadPoolExecutor = ThreadPooUtil.createCommonPool();

        // 2、初始化文件路径和内容
        String path = String.format(CSV_FILE_ABSOLUTE_PATH, "base-" + todayDate);
        // 防止重复运行报错
        FileUtil.deleteFile(path);
        FileUtil.writeStringToFile(path, ReflectUtil.getAllDescriptionFieldAnnotationValue(FundBean.class, ","), true);

        // 3、获取全量基金id，构建上下文信息，往线程池里提交查询任务
        Set<String> allIds = FundUtil.getAllFundIdsFromWeb();
        FundHandlerContext.Builder builder = new FundHandlerContext.Builder();
        FundHandlerContext context = builder
                .setAllIdCount(allIds.size())
                .setDate(todayDate)
                .setPath(String.format(CSV_FILE_ABSOLUTE_PATH, "base-" + todayDate))
                .setWriteCsv(true)
                .build();
        FundBeanFactory.getInstance().updateInstanceContext(context);
        for (String id : allIds) {
            FundDataGetRunnable task = new FundDataGetRunnable(id);
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
    }

    private static void testCSV(String testId) {
        if (StringUtil.isBlank(testId)) {
            return;
        }

        String todayDate = DateUtil.localDateToString(DateUtil.getCurrentDate());
        String path = String.format(CSV_FILE_ABSOLUTE_PATH, "test-" + todayDate);
        FileUtil.deleteFile(path);
        FileUtil.writeStringToFile(path, ReflectUtil.getAllDescriptionFieldAnnotationValue(FundBean.class, ","), true);

        FundHandlerContext.Builder builder = new FundHandlerContext.Builder();
        FundHandlerContext context = builder
                .setAllIdCount(1)
                .setDate(todayDate)
                .setPath(path)
                .setWriteCsv(true)
                .build();
        FundBeanFactory.getInstance().updateInstanceContext(context);

        FundDataGetRunnable task = new FundDataGetRunnable(testId);
        Thread thread = new Thread(task);
        thread.start();
    }
}
