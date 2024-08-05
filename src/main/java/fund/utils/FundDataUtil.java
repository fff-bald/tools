package fund.utils;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import utils.LogUtil;

import java.util.Collections;
import java.util.List;

import static fund.constant.FundConstant.LOG_NAME;

/**
 * 数据处理工具类
 */
public class FundDataUtil {

    /**
     * 按照一定策略，修复每日数据
     *
     * @param bean
     */
    public static void repairData(FundBean bean) {
        repairDayData(bean);
    }

    public static boolean checkExist(FundDayBean newDayBean, List<FundDayBean> dayBeans) {
        for (FundDayBean dayBean : dayBeans) {
            if (dayBean.getDate().equals(newDayBean.getDate())) {
                return true;
            }
        }
        return false;
    }

    // ---------- private ----------

    /**
     * 按照一定的策略，对每日数据进行整理
     *
     * @param bean
     */
    private static void repairDayData(FundBean bean) {
        List<FundDayBean> dayBeanList = bean.getDayBeanList();

        // 排序
        Collections.sort(dayBeanList);

        if (dayBeanList.isEmpty()) {
            LogUtil.error(LOG_NAME, "【%s】基金每日数据为空，%s", bean.getId(), bean);
            return;
        }

        // 第一天如果也没有数据可以直接设置为1
        FundDayBean startDay = dayBeanList.get(dayBeanList.size() - 1);
        if (startDay.getAllPrize() == Double.MIN_VALUE) {
            startDay.setAllPrize(1);
        }
        if (startDay.getPrice() == Double.MIN_VALUE) {
            startDay.setPrice(1);
        }
        if (startDay.getChange() == Double.MIN_VALUE) {
            startDay.setChange(0);
        }

        for (int i = dayBeanList.size() - 1; i >= 0; i--) {
            FundDayBean dayBean = dayBeanList.get(i);

            // 尝试修复累计净值数据
            if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                boolean isSuccess = false;
                // 1、尝试用今天的变化值和昨天的累积净值修复数据
                if (dayBean.getChange() != Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                    FundDayBean preDayBean = dayBeanList.get(i + 1);
                    if (preDayBean.getAllPrize() != Double.MIN_VALUE) {
                        dayBean.setAllPrize(preDayBean.getAllPrize() * (1 + dayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 2、尝试用明天的累积净值和变化值修复数据
                FundDayBean lastDayBean = dayBeanList.get(i - 1);
                if (lastDayBean.getChange() != Double.MIN_VALUE && lastDayBean.getAllPrize() != Double.MIN_VALUE) {
                    isSuccess = true;
                    dayBean.setAllPrize(lastDayBean.getAllPrize() / (1 + lastDayBean.getChange()));
                }
            }

            // 尝试修复当天变化值
            if (dayBean.getChange() == Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                FundDayBean preDayBean = dayBeanList.get(i + 1);
                if (dayBean.getAllPrize() != Double.MIN_VALUE && preDayBean.getAllPrize() != Double.MIN_VALUE) {
                    dayBean.setChange((dayBean.getAllPrize() - preDayBean.getAllPrize()) / preDayBean.getAllPrize());
                }
            }

            // ---------- 输出处理过还不合法的数据 ----------
            if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                LogUtil.error(LOG_NAME, "【%s】累计净值异常，%s", bean.getId(), dayBean);
            }
        }
    }
}
