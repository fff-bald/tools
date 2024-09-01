package fund.handler;

import fund.bean.FundBean;
import fund.bean.FundMonthBean;
import model.Pair;
import utils.CollectionUtil;
import utils.NewUtil;

import java.time.LocalDate;
import java.util.Map;

/**
 * 整体数据分析处理器
 */
public class AnalyzeHandler extends AbstractFundHandler {

    /**
     * 每月统计 长债&中短债 基金数量
     * （年份*100+月份，（基金总数，月度收益为负的基金总数））
     */
    private final Map<Integer, Pair<Integer, Integer>> monthChangeCountMap = NewUtil.treeMap();

    /**
     * 最近一个月，长债&中短债 基金收益分布
     */
    private final Map<Double, Integer> newMonthChangeCountMap = NewUtil.treeMap();

    AnalyzeHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
//        synchronized (this) {
//            statisticsLastMonthChangeCount(bean, 3);
//            statisticsNewMonthChangeCount(bean);
//        }
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

        count = Math.min(count, bean.getMonthBeanList().size());
        for (int i = 0; i < count; i++) {
            FundMonthBean monthBean = bean.getMonthBeanList().get(count);

            if (monthBean.getChange() / bean.getMonthAvgChange() > 10) {
                continue;
            }

            int dateKey = monthBean.getYear() * 100 + monthBean.getMonth();
            Pair<Integer, Integer> counter = CollectionUtil.computeIfAbsentAndReturnNewValue(monthChangeCountMap, dateKey, key -> new Pair(0, 0));
            counter.setFirst(counter.getFirst() + 1);
            counter.setSecond(counter.getSecond() + 1);
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

        LocalDate today = LocalDate.now();

        if (bean.getMonthBeanList().isEmpty()) {
            return;
        }

        FundMonthBean monthBean = bean.getMonthBeanList().get(0);
        if (today.getYear() != monthBean.getYear() || today.getMonthValue() != monthBean.getMonth()) {
            return;
        }

        int changeValue = (int) (monthBean.getChange() * 10);
        double changeKey = changeValue * 1d / 10;
        Integer updateValue = newMonthChangeCountMap.getOrDefault(changeKey, 0);
        newMonthChangeCountMap.put(changeKey, updateValue);
    }

    // ---------- get ----------


    public Map<Double, Integer> getNewMonthChangeCountMap() {
        return newMonthChangeCountMap;
    }

    public Map<Integer, Pair<Integer, Integer>> getMonthChangeCountMap() {
        return monthChangeCountMap;
    }
}
