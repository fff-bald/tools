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

        // 如果每日数据为空，记录错误日志并返回
        if (dayBeanList.isEmpty()) {
            bean.setFailReason("基金每日数据为空");
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

        if (dayBeanList.size() == 1) {
            return;
        }

        boolean hasError = false;

        // 从第一天开始，列表从前向后修复数据
        for (int i = 0; i < dayBeanList.size() - 1; i++) {
            FundDayBean dayBean = dayBeanList.get(i);

            // 尝试修复单位净值数据
            if (dayBean.getPrice() == Double.MIN_VALUE) {
                boolean isSuccess = false;

                // 第一次尝试：今天单位净值 = 昨天单位值 * (今天变化值 + 1)
                if (dayBean.getChange() != Double.MIN_VALUE) {
                    FundDayBean preDayBean = dayBeanList.get(i + 1);
                    if (preDayBean.getPrice() != Double.MIN_VALUE) {
                        dayBean.setPrice(preDayBean.getPrice() * (1 + dayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 第二次尝试：今天单位净值 = 明天单位值 / (明天变化值 + 1)
                if (!isSuccess && i - 1 >= 0) {
                    FundDayBean nextDayBean = dayBeanList.get(i - 1);
                    if (nextDayBean.getChange() != Double.MIN_VALUE && nextDayBean.getPrice() != Double.MIN_VALUE) {
                        dayBean.setPrice(nextDayBean.getPrice() / (1 + nextDayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 如果仍然无法修复
                if (!isSuccess) {
                    hasError = true;
                }
            }

            // 尝试修复当天变化值
            if (dayBean.getChange() == Double.MIN_VALUE) {
                FundDayBean preDayBean = dayBeanList.get(i + 1);
                if (dayBean.getPrice() != Double.MIN_VALUE && preDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setChange((dayBean.getPrice() - preDayBean.getPrice()) / preDayBean.getPrice());
                } else {
                    // 如果无法修复
                    hasError = true;
                }
            }
        }

        if (!hasError) {
            return;
        }

        hasError = false;

        // 从最后一天开始，列表从后向前向前修复数据
        for (int i = dayBeanList.size() - 1; i >= 0; i--) {
            FundDayBean dayBean = dayBeanList.get(i);

            // 尝试修复单位净值数据
            if (dayBean.getPrice() == Double.MIN_VALUE) {
                boolean isSuccess = false;

                // 第一次尝试：今天单位净值 = 昨天单位值 * (今天变化值 + 1)
                if (dayBean.getChange() != Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                    FundDayBean preDayBean = dayBeanList.get(i + 1);
                    if (preDayBean.getPrice() != Double.MIN_VALUE) {
                        dayBean.setPrice(preDayBean.getPrice() * (1 + dayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 第二次尝试：今天单位净值 = 明天单位值 / (明天变化值 + 1)
                if (!isSuccess && i - 1 >= 0) {
                    FundDayBean nextDayBean = dayBeanList.get(i - 1);
                    if (nextDayBean.getChange() != Double.MIN_VALUE && nextDayBean.getPrice() != Double.MIN_VALUE) {
                        dayBean.setPrice(nextDayBean.getPrice() / (1 + nextDayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 如果仍然无法修复
                if (!isSuccess) {
                    hasError = true;
                }
            }

            // 尝试修复当天变化值
            if (dayBean.getChange() == Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                FundDayBean preDayBean = dayBeanList.get(i + 1);
                if (dayBean.getPrice() != Double.MIN_VALUE && preDayBean.getPrice() != Double.MIN_VALUE) {
                    dayBean.setChange((dayBean.getPrice() - preDayBean.getPrice()) / preDayBean.getPrice());
                } else {
                    // 如果无法修复
                    hasError = true;
                }
            }
        }

        // 给无法修复的数据加上默认值
        if (hasError) {
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
}
