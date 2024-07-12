package funddata.task;

import funddata.FundDataBeanFactory;
import funddata.FundDataCollationUtil;
import funddata.bean.FundDataBean;
import utils.FileUtil;
import utils.LogUtil;
import utils.NewUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.List;

import static funddata.constant.FundDataConstant.FILE_ABSOLUTE_PATH;
import static funddata.constant.FundDataConstant.LOG_NAME;

/**
 * @author cjl
 * @since 2024/7/12 21:42
 */
public class FundDataGetRunnable implements Runnable {
    private static final String PATH = String.format(FILE_ABSOLUTE_PATH, "base-" + TimeUtil.YYYY_MM_DD_SDF.format(new Date()));
    private static final List<FundDataBean> RES_LIST = NewUtil.arrayList();
    private String id;
    private boolean needSave = false;

    public FundDataGetRunnable(String id, boolean needSave) {
        this.id = id;
        this.needSave = needSave;
    }

    @Override
    public void run() {
        long start = TimeUtil.now();
        try {
            FundDataBeanFactory factory = FundDataBeanFactory.getInstance();
            FundDataBean bean = factory.createBean(this.id);
            FileUtil.writeStringToFile(PATH, "'" + FundDataCollationUtil.getAllFieldValuesExceptList(bean), true);
            if (needSave) {
                RES_LIST.add(bean);
            }
        } catch (Exception e) {
            LogUtil.error(LOG_NAME, "【%s】发生异常", this.id);
            e.printStackTrace();
        }
        long endTime = TimeUtil.now();
        LogUtil.info(LOG_NAME, "【%s】处理完成，耗时：%s(ms)", this.id, endTime - start);
    }
}
