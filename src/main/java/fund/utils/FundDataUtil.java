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
     * 修复基金每日数据
     *
     * @param bean 基金数据对象
     */
    private static void repairDayData(FundBean bean) {
        List<FundDayBean> dayBeanList = bean.getDayBeanList();

        // 对每日数据进行日期排序，确保数据按日期顺序排列
        Collections.sort(dayBeanList);

        // 如果每日数据为空，记录错误日志并返回
        if (dayBeanList.isEmpty()) {
            LogUtil.error(LOG_NAME, "【%s】基金每日数据为空", bean.getId());
            return;
        }

        // 修复最早一天的数据，如果累计净值、单位净值、变化值为 Double.MIN_VALUE，则设置默认值
        FundDayBean startDay = dayBeanList.get(dayBeanList.size() - 1);
        if (startDay.getAllPrize() == Double.MIN_VALUE) {
            startDay.setAllPrize(1);
        }
        if (startDay.getChange() == Double.MIN_VALUE) {
            startDay.setChange(0);
        }
        if (startDay.getPrice() == Double.MIN_VALUE) {
            startDay.setPrice(1);
        }

        // 从最后一天开始，逐日向前修复数据
        for (int i = dayBeanList.size() - 1; i >= 0; i--) {
            FundDayBean dayBean = dayBeanList.get(i);

            // 尝试修复累计净值数据
            if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                boolean isSuccess = false;

                // 尝试用当天的变化值和前一天的累计净值修复数据
                if (dayBean.getChange() != Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                    FundDayBean preDayBean = dayBeanList.get(i + 1);
                    if (preDayBean.getAllPrize() != Double.MIN_VALUE) {
                        dayBean.setAllPrize(preDayBean.getAllPrize() * (1 + dayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 如果上述方法失败，尝试用后一天的累计净值和变化值修复数据
                if (!isSuccess && i - 1 >= 0) {
                    FundDayBean nextDayBean = dayBeanList.get(i - 1);
                    if (nextDayBean.getChange() != Double.MIN_VALUE && nextDayBean.getAllPrize() != Double.MIN_VALUE) {
                        dayBean.setAllPrize(nextDayBean.getAllPrize() / (1 + nextDayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 如果仍然无法修复，记录错误日志
                if (!isSuccess) {
                    LogUtil.error(LOG_NAME, "【%s】累计净值无法修复，日期：%s", bean.getId(), dayBean.getDate());
                }
            }

            // 尝试修复当天变化值
            if (dayBean.getChange() == Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                FundDayBean preDayBean = dayBeanList.get(i + 1);
                if (dayBean.getAllPrize() != Double.MIN_VALUE && preDayBean.getAllPrize() != Double.MIN_VALUE) {
                    dayBean.setChange((dayBean.getAllPrize() - preDayBean.getAllPrize()) / preDayBean.getAllPrize());
                } else {
                    // 如果无法修复，记录错误日志
                    LogUtil.error(LOG_NAME, "【%s】单日变化率无法修复，日期：%s", bean.getId(), dayBean.getDate());
                }
            }
        }
    }
}
