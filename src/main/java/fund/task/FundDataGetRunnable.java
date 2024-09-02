package fund.task;

import fund.FundBeanFactory;
import fund.FundHandlerContext;
import fund.bean.FundBean;
import fund.utils.FundUtil;
import utils.ExceptionUtil;
import utils.LogUtil;
import utils.TimeUtil;

/**
 * @author cjl
 * @since 2024/7/12 21:42
 */
public class FundDataGetRunnable implements Runnable {
    private final String id;

    public FundDataGetRunnable(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            long startTime = TimeUtil.now();
            FundBeanFactory factory = FundBeanFactory.getInstance();
            FundBean bean = factory.createBean(this.id);
            if (FundUtil.checkFinish(bean)) {
                FundHandlerContext instanceContext = factory.getInstanceContext();
                LogUtil.info("【{}】任务完成，耗时：{}(ms)，当前任务完成数：{}"
                        , this.id, TimeUtil.now() - startTime, instanceContext.getFinishCounter().get());
            } else {
                LogUtil.warn("【{}】任务失败，状态：{}，原因：{}"
                        , this.id, bean.getState(), bean.getFailReason());
            }
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", this.id, ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
