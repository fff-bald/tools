package fund.task;

import fund.FundBeanFactory;
import fund.bean.FundBean;
import fund.utils.FundUtil;
import utils.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cjl
 * @since 2024/7/12 21:42
 */
public class FundDataGetRunnable implements Runnable {

    // 结果容器
    private static final List<FundBean> RES_LIST = NewUtil.arrayList();
    // 任务完成数量计数器
    private static final AtomicInteger FINISH_COUNTER = new AtomicInteger(0);

    private final String path;
    private final String id;
    private final String date;
    private boolean needSave = false;

    public FundDataGetRunnable(String date, String path, String id, boolean needSave) {
        this.id = id;
        this.needSave = needSave;
        this.path = path;
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
                FileUtil.writeStringToFile(this.path, "'" + ReflectUtil.getAllDescriptionFieldsValue(bean), true);
                if (this.needSave) {
                    RES_LIST.add(bean);
                }
                int finishCount = FINISH_COUNTER.incrementAndGet();
                LogUtil.info("【%s】任务完成，耗时：%s(ms)，当前任务完成数：%s"
                        , this.id, TimeUtil.now() - startTime, finishCount);
            } else {
                LogUtil.info("【%s】任务失败，状态：%s，原因：%s"
                        , this.id, bean.getState(), bean.getFailReason());
            }
        } catch (Exception e) {
            LogUtil.error("【%s】异常信息：%s", this.id, ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
