package fund.task;

import fund.FundBeanFactory;
import fund.bean.FundBean;
import fund.utils.FundUtil;
import tag.DescriptionField;
import utils.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cjl
 * @since 2024/7/12 21:42
 */
public class FundDataGetRunnable implements Runnable {

    // 结果容器
    private static final List<FundBean> RES_LIST = NewUtil.arraySycnList();
    // 任务完成数量计数器
    private static final AtomicInteger FINISH_COUNTER = new AtomicInteger(0);

    private final String id;
    private final String date;
    private boolean needReserve = false;
    private String path;

    public FundDataGetRunnable(String date, String id) {
        this.id = id;
        this.needReserve = true;
        this.date = date;
    }

    public FundDataGetRunnable(String date, String path, String id) {
        this.id = id;
        this.path = path;
        this.date = date;
    }

    @Override
    public void run() {
        try {
            long startTime = TimeUtil.now();
            FundBeanFactory factory = FundBeanFactory.getInstance();
            FundBean bean = factory.createBean(this.id, this.date);
            if (FundUtil.checkFinish(bean)) {
                if (this.needReserve) {
                    RES_LIST.add(bean);
                } else {
                    // 不保留的话直接追加写入
                    FileUtil.writeStringToFile(this.path, "'" +
                            ReflectUtil.getAllFieldValue(bean, DescriptionField.class, ","), true);
                }
                int finishCount = FINISH_COUNTER.incrementAndGet();
                LogUtil.info("【{}】任务完成，耗时：{}(ms)，当前任务完成数：{}"
                        , this.id, TimeUtil.now() - startTime, finishCount);
            } else {
                LogUtil.warn("【{}】任务失败，状态：{}，原因：{}"
                        , this.id, bean.getState(), bean.getFailReason());
            }
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", this.id, ExceptionUtil.getStackTraceAsString(e));
        }
    }

    public static List<FundBean> getResList() {
        return RES_LIST;
    }
}
