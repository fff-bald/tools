package process.fund.utils;

import model.CommonExcelModel;
import model.Pair;
import process.fund.FundHandlerContext;
import process.fund.bean.FundDayBean;
import process.fund.bean.FundMonthBean;
import process.fund.model.FundDataExcelModel;
import utils.CollectionUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static utils.DateUtil.YYYY_MM_DD_DTF;

/**
 * 数据计算工具类
 *
 * @author cjl
 * @since 2024/8/4 18:18
 */
public class FundCalUtil {

    // ---------- public ----------

    /**
     * 计算一段时间的收益率，算法：（最后一天累计净值 - 首天累计净值）/ 首天单位净值
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
        return (endPrice - startPrice) / startDayBean.getPrice() * 100;
    }

    /**
     * 计算最大回撤
     *
     * @return
     */
    public static double calMostReduceRate(List<FundDayBean> netValues, LocalDate markDate) {
        // 检查输入列表是否为空或只有一个元素
        if (netValues == null || netValues.isEmpty() || netValues.size() == 1) {
            return 0.0;
        }

        // 初始峰值为时间最早的值（列表最后一个元素）
        double peak = netValues.get(netValues.size() - 1).getAllPrize();
        double peakPrice = netValues.get(netValues.size() - 1).getPrice();
        // 最大回撤初始化为0
        double maxDrawDown = 0.0;

        // 从倒数第二天开始，逐日向前遍历
        for (int i = netValues.size() - 2; i >= 0; i--) {
            FundDayBean fundDayBean = netValues.get(i);
            // 如果 markDate 不为空，只查找到markDate之前的数据，包括 markDate 当天
            if (markDate != null) {
                LocalDate dayBeanDate = LocalDate.parse(fundDayBean.getDate(), YYYY_MM_DD_DTF);
                if (markDate.isAfter(dayBeanDate)) {
                    continue;
                }
            }
            double netValue = fundDayBean.getAllPrize();

            // 如果当前净值高于之前的峰值，则更新峰值
            if (netValue > peak) {
                peak = netValue;
                peakPrice = netValues.get(i).getPrice();
            } else {
                // 回撤公式：（峰值当天累计净值 - 今天累计净值）/峰值当天单位净值
                double drawDown = (peak - netValue) / peakPrice;
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
     * @param nums 输入的数字列表
     * @return 标准差
     */
    public static double calculateStandardDeviation(List<Double> nums) {
        // 检查输入列表是否为空
        if (nums == null || nums.isEmpty()) {
            return 0;
        }

        double sum = 0.0;
        double sumOfSquares = 0.0;
        int size = nums.size();

        // 一次遍历同时计算总和和平方和
        for (double num : nums) {
            sum += num;
            sumOfSquares += num * num;
        }

        double mean = sum / size;
        double variance = (sumOfSquares / size) - (mean * mean);

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

    /**
     * 统计债券基金月度情况
     *
     * @param context
     * @return
     */
    public static List<Object> calculateMonthlyAnalysis(FundHandlerContext context) {
        // 定义字符串常量
        final String FUND_TYPE_LIMIT = "基金类别=长债&中短债";
        final String MAX_CHANGE_LIMIT = "月涨跌幅异常：<10";
        final String MONTH_CHANGE_LABEL = "月涨跌幅范围";
        final String TOTAL_NUM_LABEL = "汇总数量";
        final String TOTAL_LABEL = "总量";
        final String INCREASE_LABEL = "上涨数";
        final String DECREASE_LABEL = "下跌数";
        final String INCREASE_RATE_LABEL = "上涨比例";

        List<Object> result = CollectionUtil.arrayList();
        LocalDate contextDate = LocalDate.parse(context.getDate());
        int contextMonth = contextDate.getMonthValue();

        // 添加标题行
        result.add(CommonExcelModel.valueOf(FUND_TYPE_LIMIT, "", MAX_CHANGE_LIMIT));
        result.add(new CommonExcelModel()); // 空行或分隔行

        // 1、当月涨跌分布
        result.add(CommonExcelModel.valueOf(contextMonth + MONTH_CHANGE_LABEL, TOTAL_NUM_LABEL, ""));
        for (Map.Entry<Double, Integer> entry : context.getNewMonthChangeCountMap().entrySet()) {
            result.add(CommonExcelModel.valueOf(entry.getKey() + "%", String.valueOf(entry.getValue()), ""));
        }

        // 2、近几月的涨跌比例
        result.add(new CommonExcelModel()); // 空行或分隔行
        result.add(CommonExcelModel.valueOf("", INCREASE_RATE_LABEL, TOTAL_LABEL, INCREASE_LABEL, DECREASE_LABEL));
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : context.getMonthChangeCountMap().entrySet()) {
            int totalCount = entry.getValue().getFirst();
            int decreaseCount = entry.getValue().getSecond();
            int increaseCount = totalCount - decreaseCount;

            result.add(CommonExcelModel.valueOf(
                    String.valueOf(entry.getKey()),
                    String.format("%.2f", (increaseCount * 1.0 / totalCount) * 100) + "%",
                    String.valueOf(totalCount),
                    String.valueOf(increaseCount),
                    String.valueOf(decreaseCount)));
        }

        return result;
    }

    /**
     * 统计长线稳健基金数据
     *
     * @param allData
     * @return
     */
    public static List<Object> countLongGoodFunds(List<Object> allData) {
        List<Object> result = CollectionUtil.arrayList();
        for (Object data : allData) {
            if (!(data instanceof FundDataExcelModel)) {
                continue;
            }
            FundDataExcelModel dataExcelModel = (FundDataExcelModel) data;

            // 规模大于1亿元
            if (dataExcelModel.getMoney() < 1) {
                continue;
            }

            // 成立年数大于3
            if (dataExcelModel.getDurationDay() < 3) {
                continue;
            }
            // 上涨月份比例大于90% && 上涨日数比例大于85%
            if (dataExcelModel.getUpMonthRate() < 90 || dataExcelModel.getUpDayRate() < 85) {
                continue;
            }
            // 月涨跌幅最大异常要在0和15之间
            if (dataExcelModel.getMonthMostChangeToAvg() < 0 || dataExcelModel.getMonthMostChangeToAvg() > 15) {
                continue;
            }
            // 五年内最大回撤小于1.2%
            if (dataExcelModel.getFiveYearMostReduceRate() > 1.2) {
                continue;
            }
            // 复利年化收益率大于3.5%
            if (dataExcelModel.getYearChangePro() < 3.5) {
                continue;
            }
            // 个人投资者占比份额为0
            if ("0.00%".equals(dataExcelModel.getPersonRate()) || "未匹配成功".equals(dataExcelModel.getPersonRate())) {
                continue;
            }
            result.add(data);
        }
        return result;
    }

    // ---------- private ----------
}
