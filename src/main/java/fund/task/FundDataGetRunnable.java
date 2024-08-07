package fund.task;

import fund.FundBeanFactory;
import fund.bean.FundBean;
import fund.utils.FundUtil;
import utils.*;

import java.util.List;

import static fund.constant.FundConstant.LOG_NAME;

/**
 * @author cjl
 * @since 2024/7/12 21:42
 */
public class FundDataGetRunnable implements Runnable {

    private static final List<FundBean> RES_LIST = NewUtil.arrayList();
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
                LogUtil.info(LOG_NAME, "【%s】任务完成，耗时：%s(ms)", this.id, TimeUtil.now() - startTime);
            } else {
                LogUtil.error(LOG_NAME, "【%s】任务失败，状态：%s", this.id, bean.getState());
            }
        } catch (Exception e) {
            LogUtil.error(LOG_NAME, "【%s】异常信息：%s", this.id, ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
