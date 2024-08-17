package fund.utils;

import fund.bean.FundDayBean;

import java.util.List;

/**
 * 数据处理工具类
 */
public class FundDataUtil {

    // ---------- public ----------

    public static boolean checkExist(FundDayBean newDayBean, List<FundDayBean> dayBeans) {
        for (FundDayBean dayBean : dayBeans) {
            if (dayBean.getDate().equals(newDayBean.getDate())) {
                return true;
            }
        }
        return false;
    }
}
