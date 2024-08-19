package fund.utils;

import fund.bean.FundDayBean;
import fund.bean.FundMonthBean;

import java.time.LocalDate;
import java.util.List;

import static utils.TimeUtil.YYYY_MM_DD_DTF;

/**
 * 数据计算工具类
 *
 * @author cjl
 * @since 2024/8/4 18:18
 */
public class FundCalUtil {

    // ---------- public ----------

    /**
     * 计算一段时间的收益率，算法：（最后一天累计净值 - 首天累计净值）/ 首天累计净值
     *
     * @param dayList 基金每日数据列表
     * @param today   当前日期
     * @param day     时间段的天数
     * @return 指定时间段内的收益率(百分比)
     */
    public static double calTimeChange(List<FundDayBean> dayList, LocalDate today, int day) {
        // 计算指定天数前的日期
        LocalDate markDate = today.minusDays(day);

        // 初始化变量
        FundDayBean endDayBean = dayList.get(0); // 假设列表按日期从近到远排序，获取最近一天的数据
        FundDayBean startDayBean = endDayBean; // 初始化为最近一天的数据

        // 查找指定天数前的基金数据
        for (FundDayBean dayBean : dayList) {
            LocalDate dayBeanDate = LocalDate.parse(dayBean.getDate(), YYYY_MM_DD_DTF);
            if (markDate.isBefore(dayBeanDate) || markDate.isEqual(dayBeanDate)) {
                startDayBean = dayBean;
            } else {
                break;
            }
        }

        // 计算收益率
        double startPrice = startDayBean.getAllPrize();
        double endPrice = endDayBean.getAllPrize();
        return (endPrice - startPrice) / startPrice * 100;
    }

    /**
     * 计算最大回撤
     *
     * @return
     */
    public static double calMostReduceRate(List<FundDayBean> netValues) {
        // 检查输入列表是否为空或只有一个元素
        if (netValues == null || netValues.isEmpty() || netValues.size() == 1) {
            return 0.0;
        }

        // 初始峰值为时间最早的值（列表最后一个元素）
        double peak = netValues.get(netValues.size() - 1).getAllPrize();
        // 最大回撤初始化为0
        double maxDrawDown = 0.0;

        // 从最后一天开始，逐日向前遍历
        for (int i = netValues.size() - 1; i >= 0; i--) {
            double netValue = netValues.get(i).getAllPrize();

            // 如果当前净值高于之前的峰值，则更新峰值
            if (netValue > peak) {
                peak = netValue;
            } else {
                // 计算当前净值与峰值之间的回撤
                double drawDown = (peak - netValue) / peak;
                // 如果当前回撤大于之前的最大回撤，则更新最大回撤
                if (drawDown > maxDrawDown) {
                    maxDrawDown = drawDown;
                }
            }
        }

        // 返回最大回撤的百分比（转换为百分比形式）
        return maxDrawDown * 100.0;
    }

    /**
     * 计算复利年化收益率（按照一年365天来算）
     *
     * @return
     */
    public static double calYearChange(long day, double startPrice, double endPrice) {
        double year = day * 1.0d / 365;
        return Math.pow(endPrice / startPrice, 1.0d / year) - 1;
    }

    /**
     * 计算标准差
     *
     * @param growthRates
     * @return
     */
    public static double calculateStandardDeviation(List<Double> growthRates) {
        double sum = 0.0;
        for (double rate : growthRates) {
            sum += rate;
        }
        double mean = sum / growthRates.size();

        double sumOfSquares = 0.0;
        for (double rate : growthRates) {
            sumOfSquares += Math.pow(rate - mean, 2);
        }
        double variance = sumOfSquares / growthRates.size();

        return Math.sqrt(variance);
    }

    public static double calMostAvgRate(List<FundMonthBean> monthBeans) {
        double sum = 0;
        double biggest = Double.MIN_VALUE;
        for (FundMonthBean monthBean : monthBeans) {
            sum += monthBean.getChange();
            biggest = Math.max(biggest, monthBean.getChange());
        }

        return biggest / (sum / monthBeans.size());
    }

    // ---------- private ----------
}
