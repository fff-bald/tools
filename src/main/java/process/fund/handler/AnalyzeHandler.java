package process.fund.handler;

import model.Pair;
import process.fund.FundHandlerContext;
import process.fund.bean.FundBean;
import process.fund.bean.FundMonthBean;
import utils.CollectionUtil;
import utils.DateUtil;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 整体数据分析处理器
 */
public class AnalyzeHandler extends AbstractFundHandler {

    AnalyzeHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        FundHandlerContext context = getContext();

        if (context.isWriteExcel()) {
            statisticsLastMonthChangeCount(bean, 5);
            statisticsNewMonthChangeCount(bean);
            statisticsNewMonthMostReduceRateCount(bean);
        }
    }

    // ---------- private ----------

    /**
     * 统计最近特定个月的月度变化占比
     *
     * @param bean
     * @param count 特定个月
     */
    private void statisticsLastMonthChangeCount(FundBean bean, int count) {

        if (!bean.getType().contains("长债") && !bean.getType().contains("中短债")) {
            return;
        }

        FundHandlerContext context = getContext();
        Map<Integer, Pair<AtomicInteger, AtomicInteger>> monthChangeCountMap = context.getMonthChangeCountMap();

        // 只记录特定几个月的数据
        LocalDate mark = LocalDate.parse(context.getDate()).minusMonths(count - 1);
        count = Math.min(count, bean.getMonthBeanList().size());
        for (int index = 0; index < count; index++) {
            FundMonthBean monthBean = bean.getMonthBeanList().get(index);

            // 太远的数据就不算了
            if (mark.getYear() > monthBean.getYear()
                    || (mark.getYear() == monthBean.getYear() && mark.getMonthValue() > monthBean.getMonth())) {
                break;
            }

            if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
                continue;
            }

            int dateKey = monthBean.getYear() * 100 + monthBean.getMonth();
            Pair<AtomicInteger, AtomicInteger> counter = CollectionUtil.computeIfAbsentAndReturnNewValueSync(monthChangeCountMap,
                    dateKey, key -> new Pair<>(new AtomicInteger(0), new AtomicInteger(0)));
            counter.getFirst().incrementAndGet();
            if (monthBean.getChange() < 0) {
                counter.getSecond().incrementAndGet();
            }
        }
    }

    /**
     * 统计最新一个月的月度变化占比
     *
     * @param bean
     */
    private void statisticsNewMonthChangeCount(FundBean bean) {

        if (!bean.getType().contains("长债") && !bean.getType().contains("中短债")) {
            return;
        }

        if (bean.getMonthBeanList().isEmpty()) {
            return;
        }

        FundHandlerContext context = getContext();
        LocalDate today = DateUtil.stringToLocalDate(context.getDate());
        FundMonthBean monthBean = bean.getMonthBeanList().get(0);
        if (today.getYear() != monthBean.getYear() || today.getMonthValue() != monthBean.getMonth()) {
            return;
        }

        if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
            return;
        }

        Map<Double, AtomicInteger> newMonthChangeCountMap = context.getNewMonthChangeCountMap();

        // 0.0%，四舍五入
        double changeKey = Math.round(monthBean.getChange() * 10.0) / 10.0;
        AtomicInteger updateValue = CollectionUtil.computeIfAbsentAndReturnNewValueSync(newMonthChangeCountMap,
                changeKey, key -> new AtomicInteger(0));
        updateValue.incrementAndGet();
    }

    /**
     * 统计这个月长债、中短债的数量 和 其中当月产生五年内最大回撤的数量
     *
     * @param bean
     */
    private void statisticsNewMonthMostReduceRateCount(FundBean bean) {
        if (!bean.getType().contains("长债") && !bean.getType().contains("中短债")) {
            return;
        }

        FundHandlerContext context = getContext();
        context.getStatisticsFundCounter().incrementAndGet();
        LocalDate today = DateUtil.stringToLocalDate(context.getDate());
        LocalDate mostReduceDate = DateUtil.stringToLocalDate(bean.getFiveYearMostReduceRateDate());

        if (today.getYear() == mostReduceDate.getYear() && today.getMonthValue() == mostReduceDate.getMonthValue()) {
            context.getStatisticsNewMonthMostReduceRateCounter().incrementAndGet();
        }
    }
}
