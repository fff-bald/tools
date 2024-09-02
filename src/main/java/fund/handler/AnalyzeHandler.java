package fund.handler;

import fund.FundHandlerContext;
import fund.bean.FundBean;
import fund.bean.FundMonthBean;
import model.Pair;
import utils.CollectionUtil;

import java.time.LocalDate;
import java.util.Map;

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
            synchronized (this) {
                statisticsLastMonthChangeCount(bean, 3);
                statisticsNewMonthChangeCount(bean);
            }
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

        Map<Integer, Pair<Integer, Integer>> monthChangeCountMap = getContext().getMonthChangeCountMap();

        count = Math.min(count, bean.getMonthBeanList().size());
        for (int index = 0; index < count; index++) {
            FundMonthBean monthBean = bean.getMonthBeanList().get(index);

            if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
                continue;
            }

            int dateKey = monthBean.getYear() * 100 + monthBean.getMonth();
            Pair<Integer, Integer> counter = CollectionUtil.computeIfAbsentAndReturnNewValue(monthChangeCountMap, dateKey, key -> new Pair<>(0, 0));
            counter.setFirst(counter.getFirst() + 1);
            if (monthBean.getChange() < 0) {
                counter.setSecond(counter.getSecond() + 1);
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

        LocalDate today = LocalDate.now();
        FundMonthBean monthBean = bean.getMonthBeanList().get(0);
        if (today.getYear() != monthBean.getYear() || today.getMonthValue() != monthBean.getMonth()) {
            return;
        }

        if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
            return;
        }

        FundHandlerContext context = getContext();
        Map<Double, Integer> newMonthChangeCountMap = context.getNewMonthChangeCountMap();

        int changeValue = (int) (monthBean.getChange() * 10);
        double changeKey = changeValue * 1d / 10;
        Integer updateValue = newMonthChangeCountMap.getOrDefault(changeKey, 0);
        newMonthChangeCountMap.put(changeKey, updateValue + 1);
    }
}
