package process.fund.handler;

import model.Pair;
import process.fund.bean.FundBean;
import process.fund.bean.FundDayBean;
import process.fund.bean.FundMonthBean;
import process.fund.utils.FundCalUtil;
import utils.CollectionUtil;
import utils.DateUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bean参数计算处理器
 */
public class CalDataHandler extends AbstractFundHandler {
    CalDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        calFundData(bean);
    }

    // ---------- private ----------

    /**
     * 根据已有的信息，计算各项指标
     *
     * @param bean
     */
    private static void calFundData(FundBean bean) {
        List<FundDayBean> dayList = bean.getDayBeanList();

        if (dayList.isEmpty()) {
            return;
        }

        FundDayBean endDay = dayList.get(0);
        FundDayBean startDay = dayList.get(dayList.size() - 1);

        // 基金存续时间
        long totalDay = DateUtil.calYearBetween(startDay.getDate(), endDay.getDate()) + 1;
        bean.setDurationDay(totalDay * 1d / 365);
        int tradingDay = dayList.size();

        // 最新一日相关信息
        LocalDate updateLocalDate = LocalDate.parse(endDay.getDate());
        bean.setUpdateTime(updateLocalDate);
        bean.setBuyState(endDay.getBuyState());
        bean.setSellState(endDay.getSellState());

        // 复利年化收益率
        bean.setYearChangePro(100 * FundCalUtil.calYearChange(totalDay, startDay.getAllPrize(), endDay.getAllPrize()));

        // 近七天收益率
        bean.setSevenDayChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 7));
        // 近一个月收益率
        bean.setMonthChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 30));
        // 近三个月收益率
        bean.setThreeMonthChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 30 * 3));
        // 近半年收益率
        bean.setSixMonthChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 30 * 6));
        // 近一年收益率
        bean.setYearChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 365));
        // 近三年收益率
        bean.setThreeYearChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 3 * 365));

        // 历史最大回撤
        bean.setMostReduceRate(FundCalUtil.calMostReduceRate(dayList, null).getSecond());
        Pair<String, Double> fiveMostReduceRatePair = FundCalUtil.calMostReduceRate(dayList, updateLocalDate.minusYears(5));
        bean.setFiveYearMostReduceRate(fiveMostReduceRatePair.getSecond());
        bean.setFiveYearMostReduceRateDate(fiveMostReduceRatePair.getFirst());

        // 上升日比例
        double upDay = 0;
        for (FundDayBean dayBean : bean.getDayBeanList()) {
            if (dayBean.getChange() >= 0) {
                upDay++;
            }
        }
        bean.setUpDayRate(100 * upDay / tradingDay);

        // 日涨跌幅标准差
        List<Double> growthRates = dayList.stream().map(FundDayBean::getChange).collect(Collectors.toList());
        bean.setDayStandardDeviation(FundCalUtil.calculateStandardDeviation(growthRates));

        // 计算月份数据
        calMonthData(bean);
    }

    /**
     * 计算跟月份相关的数据
     *
     * @param bean
     */
    public static void calMonthData(FundBean bean) {
        List<FundDayBean> dayList = bean.getDayBeanList();
        List<FundMonthBean> monthBeans = CollectionUtil.arrayList();
        Map<Integer, FundDayBean> monthlyGrowth = CollectionUtil.hashMap();
        for (int index = dayList.size() - 1; index >= 0; index--) {
            FundDayBean dayBean = dayList.get(index);
            LocalDate localDate = DateUtil.stringToLocalDate(dayBean.getDate());
            int yearMonth = localDate.getYear() * 100 + localDate.getMonthValue();

            if (!monthlyGrowth.containsKey(yearMonth)) {
                // 如果是新月份
                if (index < dayList.size() - 1) {
                    // 记录上个月最后一天的值
                    monthlyGrowth.put(yearMonth, dayList.get(index + 1));
                } else {
                    // 记录月初的值
                    monthlyGrowth.put(yearMonth, dayBean);
                }
            }

            // 检查是否是月末，计算增长量
            if (index == 0 || DateUtil.stringToLocalDate(dayList.get(index - 1).getDate()).getMonthValue() != localDate.getMonthValue()) {
                FundMonthBean monthBean = FundMonthBean.valueOf(localDate.getYear(), localDate.getMonthValue(), monthlyGrowth.get(yearMonth), dayBean);
                monthBeans.add(monthBean);
            }
        }

        Collections.sort(monthBeans);
        // 月份数据
        bean.setMonthBeanList(monthBeans);

        double upMonthNum = 0;
        double monthChangeSum = 0;
        for (FundMonthBean monthBean : monthBeans) {
            if (monthBean.getChange() >= 0) {
                upMonthNum++;
            }
            monthChangeSum += monthBean.getChange();
        }
        // 上升月比例
        bean.setUpMonthRate(100 * upMonthNum / monthBeans.size());
        // 月变化比例平均值
        bean.setMonthAvgChange(monthChangeSum / monthBeans.size());

        List<Double> growthRates = monthBeans.stream().map(FundMonthBean::getChange).collect(Collectors.toList());
        // 月涨跌幅标准差
        bean.setMonthStandardDeviation(FundCalUtil.calculateStandardDeviation(growthRates));

        // 月涨跌幅最大异常
        bean.setMonthMostChangeToAvg(FundCalUtil.calMostAvgRate(monthBeans));
    }
}