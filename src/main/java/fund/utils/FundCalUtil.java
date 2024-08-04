package fund.utils;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import fund.bean.FundMonthBean;
import utils.NewUtil;
import utils.TimeUtil;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.TimeUtil.YYYY_MM_DD_DTF;

/**
 * 数据计算工具类
 *
 * @author cjl
 * @since 2024/8/4 18:18
 */
public class FundCalUtil {

    /**
     * 设置三年的年化收益率，如果不够三年也按三年来算
     *
     * @param dayList
     * @param time
     * @return
     */
    public static void setThreeYearChange(FundBean bean, List<FundDayBean> dayList, Date time) {
        LocalDate today = TimeUtil.convertDateToLocalDate(time);
        LocalDate ago = today.minusYears(3);
        FundDayBean startDateBean = null;
        FundDayBean endDayBean = dayList.get(0);
        for (FundDayBean dayBean : dayList) {
            LocalDate localDate = LocalDate.parse(dayBean.getDate(), YYYY_MM_DD_DTF);
            if (ago.isBefore(localDate) || ago.isEqual(localDate)) {
                startDateBean = dayBean;
            } else {
                break;
            }
        }

        // 年化收益率
        bean.setThreeYearChange(100 * (endDayBean.getAllPrize() - startDateBean.getAllPrize()) / startDateBean.getAllPrize() / 3);
        // 复利年化收益率
        bean.setThreeYearChangePro(100 * calYearChange(3 * 365, startDateBean.getAllPrize(), endDayBean.getAllPrize()));
    }

    /**
     * 计算最大回撤
     *
     * @return
     */
    public static double calMostReduceRate(List<FundDayBean> netValues) {

        if (netValues == null || netValues.isEmpty() || netValues.size() == 1) {
            return 0.0;
        }

        double peak = netValues.get(0).getAllPrize(); // 初始峰值为列表的第一个值
        double maxDrawDown = 0.0; // 最大回撤初始化为0

        for (FundDayBean dayBean : netValues) {
            double netValue = dayBean.getAllPrize();
            if (netValue > peak) {
                // 如果当前净值高于之前的峰值，则更新峰值
                peak = netValue;
            } else {
                // 计算当前净值与峰值之间的回撤
                double drawDown = (peak - netValue) / peak;
                if (drawDown > maxDrawDown) {
                    // 如果当前回撤大于之前的最大回撤，则更新最大回撤
                    maxDrawDown = drawDown;
                }
            }
        }

        // 返回最大回撤的百分比（转换为百分比形式）
        return maxDrawDown * 100.0;
    }

    /**
     * 计算跟月份相关的数据
     *
     * @param bean
     */
    public static void calMonthData(FundBean bean) {
        List<FundDayBean> dayList = bean.getDayBeanList();
        List<FundMonthBean> monthBeans = NewUtil.arrayList();
        Map<Integer, FundDayBean> monthlyGrowth = NewUtil.hashMap();
        for (int index = dayList.size() - 1; index >= 0; index--) {
            FundDayBean dayBean = dayList.get(index);
            LocalDate localDate = LocalDate.parse(dayBean.getDate(), YYYY_MM_DD_DTF);
            Double value = dayBean.getAllPrize();
            int yearMonth = localDate.getYear() * 100 + localDate.getMonthValue();

            if (!monthlyGrowth.containsKey(yearMonth)) {
                // 如果是新月份，记录月初的值
                monthlyGrowth.put(yearMonth, dayBean);
            }

            // 检查是否是月末，计算增长量
            if (index == 0 || LocalDate.parse(dayList.get(index - 1).getDate(), YYYY_MM_DD_DTF).getMonthValue() != localDate.getMonthValue()) {
                FundMonthBean monthBean = FundMonthBean.valueOf(localDate.getYear(),
                        localDate.getMonthValue(), monthlyGrowth.get(yearMonth), dayBean);
                monthBeans.add(monthBean);
            }
        }

        bean.setMonthBeanList(monthBeans);

        double upMonthNum = 0;
        for (FundMonthBean monthBean : monthBeans) {
            if (monthBean.getChange() >= 0) {
                upMonthNum++;
            }
        }
        bean.setUpMonthRate(100 * upMonthNum / monthBeans.size());

        List<Double> growthRates = monthBeans.stream().map(FundMonthBean::getChange).collect(Collectors.toList());
        bean.setMonthStandardDeviation(calculateStandardDeviation(growthRates));
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
}
