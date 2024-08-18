package fund.handler;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import fund.utils.FundCalUtil;
import utils.TimeUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数计算处理器
 */
public class CalDataHandler extends AbstractFundBeanHandler {
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
        long totalDay = TimeUtil.calYearBetween(startDay.getDate(), endDay.getDate()) + 1;
        bean.setDurationDay((int) totalDay);
        int tradingDay = dayList.size();

        // 最新一日相关信息
        bean.setUpdateTime(LocalDate.parse(endDay.getDate()));
        bean.setBuyState(endDay.getBuyState());
        bean.setSellState(endDay.getSellState());

        // 复利年化收益率
        bean.setYearChangePro(100 * FundCalUtil.calYearChange(totalDay, startDay.getAllPrize(), endDay.getAllPrize()));

        // 近七天收益率
        bean.setSevenDayChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 7));
        // 近一个月收益率
        bean.setMonthChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 30));
        // 近半年收益率
        bean.setSixMonthChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 30 * 6));
        // 近一年收益率
        bean.setYearChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 365));
        // 近三年收益率
        bean.setThreeYearChange(FundCalUtil.calTimeChange(dayList, bean.getUpdateTime(), 3 * 365));

        // 历史最大回撤
        bean.setMostReduceRate(FundCalUtil.calMostReduceRate(dayList));

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
        FundCalUtil.calMonthData(bean);
    }
}
