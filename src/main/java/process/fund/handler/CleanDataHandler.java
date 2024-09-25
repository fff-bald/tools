package process.fund.handler;

import process.fund.bean.FundBean;
import process.fund.bean.FundDayBean;
import utils.LogUtil;

import java.util.Collections;
import java.util.List;

/**
 * 按照一定策略，对Bean数据进行清洗
 */
public class CleanDataHandler extends AbstractFundHandler {
    CleanDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        repairData(bean);
    }

    // ---------- private ----------

    /**
     * 修复数据
     *
     * @param bean
     */
    private static void repairData(FundBean bean) {
        repairDayData(bean);
    }

    /**
     * 按照一定策略，修复基金每日数据
     *
     * @param bean 基金数据对象
     */
    private static void repairDayData(FundBean bean) {
        List<FundDayBean> dayBeanList = bean.getDayBeanList();

        // 对每日数据进行日期排序，确保数据按日期顺序排列
        Collections.sort(dayBeanList);

        if (dayBeanList.isEmpty()) {
            // 如果每日数据为空，记录错误日志并返回
            bean.setFailReason("基金每日数据为空");
            return;
        }

        // ~给最早一天的数据设置默认值：
        // 如果累计净值、单位净值、变化值为 Double.MIN_VALUE，则设置默认值
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

        if (dayBeanList.size() == 1) {
            return;
        }

        boolean hasError = false;
        boolean isWarn = false;
        // ~列表从前向后修复数据，首日数据不需要检查
        for (int i = 0; i < dayBeanList.size() - 1; i++) {
            FundDayBean dayBean = dayBeanList.get(i);

            if (!repairDayDataByOtherDays(dayBean, dayBeanList.get(i + 1), i - 1 >= 0 ? dayBeanList.get(i - 1) : null)) {
                hasError = true;
            }
        }

        // ~从倒数第二天开始，列表从后向前修复数据
        if (hasError) {
            for (int i = dayBeanList.size() - 2; i >= 0; i--) {
                FundDayBean dayBean = dayBeanList.get(i);
                if (!repairDayDataByOtherDays(dayBean, dayBeanList.get(i + 1), i - 1 >= 0 ? dayBeanList.get(i - 1) : null)) {
                    isWarn = true;
                }

                // 遍历的时候把累计净值有问题的也标记一下
                if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                    isWarn = true;
                }
            }
        }

        // ~给无法修复的数据加上默认值
        if (isWarn) {
            for (int i = dayBeanList.size() - 2; i >= 0; i--) {
                FundDayBean dayBean = dayBeanList.get(i);
                FundDayBean preDayBean = dayBeanList.get(i + 1);

                if (dayBean.getPrice() == Double.MIN_VALUE) {
                    dayBean.setPrice(preDayBean.getPrice());
                    LogUtil.warn("【{}】单位净值无法修复，日期：{}", dayBean.getId(), dayBean.getDate());
                }

                if (dayBean.getChange() == Double.MIN_VALUE) {
                    dayBean.setChange(0);
                    LogUtil.warn("【{}】单日变化率无法修复，日期：{}", dayBean.getId(), dayBean.getDate());
                }

                if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                    dayBean.setAllPrize(preDayBean.getAllPrize());
                    LogUtil.warn("【{}】累计净值无法修复，日期：{}", dayBean.getId(), dayBean.getDate());
                }
            }
            LogUtil.warn("【{}】基金每日数据存在无法修复的情况", bean.getId());
        }
    }

    /**
     * 根据前后数据尝试修复当天数据
     *
     * @param dayBean     需要被那一天的数据
     * @param preDayBean  被修复数据前一日的数据
     * @param nextDayBean 被修复数据后一天的数据
     * @return 是否修复成功
     */
    private static boolean repairDayDataByOtherDays(FundDayBean dayBean, FundDayBean preDayBean, FundDayBean nextDayBean) {
        boolean isSuccess = true;

        // 尝试修复单位净值数据
        if (dayBean.getPrice() == Double.MIN_VALUE) {
            isSuccess = false;
            // 第一次尝试：今天单位净值 = 昨天单位值 * (今天变化值 + 1)
            if (preDayBean != null && dayBean.getChange() != Double.MIN_VALUE) {
                if (preDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setPrice(preDayBean.getPrice() * (1 + dayBean.getChange()));
                    isSuccess = true;
                }
            }

            // 第二次尝试：今天单位净值 = 明天单位值 / (明天变化值 + 1)
            if (!isSuccess && nextDayBean != null) {
                if (nextDayBean.getChange() != Double.MIN_VALUE && nextDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setPrice(nextDayBean.getPrice() / (1 + nextDayBean.getChange()));
                    isSuccess = true;
                }
            }
        }

        // 尝试修复当天变化值
        if (dayBean.getChange() == Double.MIN_VALUE) {
            isSuccess = false;
            if (preDayBean != null && dayBean.getPrice() != Double.MIN_VALUE && preDayBean.getPrice() != Double.MIN_VALUE) {
                dayBean.setChange((dayBean.getPrice() - preDayBean.getPrice()) / preDayBean.getPrice());
                isSuccess = true;
            }
        }

        // 尝试修复当天累计净值
        if (dayBean.getAllPrize() == Double.MIN_VALUE) {
            isSuccess = false;

            // 通过邻近的单位净值计算当天累计净值，但这个方法计算出的修复值会受（单位净值分红拆分）影响，导致误差
            // 但考虑到分红拆分是少数，误差影响对最终结果应该不大
            if (dayBean.getPrice() != Double.MIN_VALUE) {
                // 公式：今天的累计净值 = 今天的单位净值 - 昨天的单位净值 + 昨天的累计净值
                if (preDayBean != null && preDayBean.getAllPrize() != Double.MIN_VALUE && preDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setAllPrize(dayBean.getPrice() - preDayBean.getPrice() + preDayBean.getAllPrize());
                    isSuccess = true;
                }

                // 公式：今天的累计净值 = 明天的累计净值 - （明天的单位净值 - 今天的单位净值）
                if (!isSuccess && nextDayBean != null && nextDayBean.getAllPrize() != Double.MIN_VALUE
                        && nextDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setAllPrize(nextDayBean.getAllPrize() - (nextDayBean.getPrice() - dayBean.getPrice()));
                    isSuccess = true;
                }
            }
        }

        return isSuccess;
    }
}
