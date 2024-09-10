package process.fund.handler;

import process.fund.FundHandlerContext;
import process.fund.bean.FundBean;
import process.fund.bean.FundMonthBean;
import model.Pair;
import utils.CollectionUtil;

import java.time.LocalDate;
import java.util.Map;

/**
 * 整体数据分析处理器
 */
public class AnalyzeHandler extends AbstractFundHandler {

    private final Object statisticsLastMonthChangeCountLock = new Object();
    private final Object statisticsNewMonthChangeCountLock = new Object();

    AnalyzeHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        FundHandlerContext context = getContext();

        if (context.isWriteExcel()) {
            statisticsLastMonthChangeCount(bean, 5);
            statisticsNewMonthChangeCount(bean);
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
        Map<Integer, Pair<Integer, Integer>> monthChangeCountMap = context.getMonthChangeCountMap();

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
            // 锁细化，只所修改到共享数据的地方
            synchronized (statisticsLastMonthChangeCountLock) {
                Pair<Integer, Integer> counter = CollectionUtil.computeIfAbsentAndReturnNewValue(monthChangeCountMap, dateKey, key -> new Pair<>(0, 0));
                counter.setFirst(counter.getFirst() + 1);
                if (monthBean.getChange() < 0) {
                    counter.setSecond(counter.getSecond() + 1);
                }
            }
        }
    }

    /**
     * 统计最新一个月的月度变化占比
     *
     * @param bean
     */
    public void statisticsNewMonthChangeCount(FundBean bean) {

        if (!bean.getType().contains("长债") && !bean.getType().contains("中短债")) {
            return;
        }

        if (bean.getMonthBeanList().isEmpty()) {
            return;
        }

        FundHandlerContext context = getContext();
        LocalDate today = LocalDate.parse(context.getDate());
        FundMonthBean monthBean = bean.getMonthBeanList().get(0);
        if (today.getYear() != monthBean.getYear() || today.getMonthValue() != monthBean.getMonth()) {
            return;
        }

        if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
            return;
        }

        Map<Double, Integer> newMonthChangeCountMap = context.getNewMonthChangeCountMap();

        // 0.0%，四舍五入
        double changeKey = Math.round(monthBean.getChange() * 10.0) / 10.0;
        // 锁细化，只所修改到共享数据的地方
        synchronized (statisticsNewMonthChangeCountLock) {
            Integer updateValue = newMonthChangeCountMap.getOrDefault(changeKey, 0);
            newMonthChangeCountMap.put(changeKey, updateValue + 1);
        }
    }
}