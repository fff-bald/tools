package fund.utils;

import fund.bean.FundDayBean;
import utils.*;

import java.util.List;

import static fund.constant.FundConstant.LOG_NAME;

/**
 * 数据库工具类
 *
 * @author cjl
 * @since 2024/8/4 13:50
 */
public class FundDataBaseUtil {

    private static final String INIT_PATH = ".\\fund_data\\%s.txt";

    public static void addData(FundDayBean dataDayBean, boolean isCheck) {
        if (isCheck && checkExistInDataBase(dataDayBean)) {
            return;
        }

        String id = dataDayBean.getId();
        dataDayBean.setPersistence(true);
        try {
            FileUtil.writeStringToFile(getFilePath(id), JsonUtil.toJson(dataDayBean), true);
        } catch (Exception e) {
            LogUtil.error(LOG_NAME, "【%s】异常信息：%s", dataDayBean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    public static void addDataList(List<FundDayBean> dayBeans, boolean isCheck) {
        for (FundDayBean dayBean : dayBeans) {
            if(!dayBean.isPersistence()) {
                addData(dayBean, isCheck);
            }
        }
    }

    public static List<FundDayBean> getData(String id) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = NewUtil.arrayList();
        for (String str : strings) {
            if (str.contains(id)) {
                try {
                    FundDayBean dayBean = JsonUtil.toObject(str, FundDayBean.class);
                    res.add(dayBean);
                } catch (Exception e) {
                    LogUtil.error(LOG_NAME, "【%s】异常信息：%s", id, ExceptionUtil.getStackTraceAsString(e));
                }
            }
        }
        return res;
    }

    /**
     * 检查该数据是否已经在数据库中存在
     *
     * @param dataDayBean
     * @return
     */
    public static boolean checkExistInDataBase(FundDayBean dataDayBean) {
        for (FundDayBean dayBean : getData(dataDayBean.getId())) {
            if (dayBean.getDate().equals(dataDayBean.getDate())) {
                return true;
            }
        }
        return false;
    }

    // ---------- private ----------

    private static String getFilePath(String id) {
        int fileNum = id.hashCode() & (128 - 1);
        return String.format(INIT_PATH, fileNum);
    }
}
