package fund.handler;

import fund.bean.FundBean;
import org.apache.commons.math3.util.Pair;
import utils.NewUtil;

import java.util.Map;

/**
 * 整体数据分析处理器
 */
public class AnalyzeDataHandler extends AbstractFundBeanHandler {

    /**
     * 长债&中短债 月份总数和月份下降总数
     * （年份*100+月份，（符合条件基金的总数，符合条件基金下降的数量））
     */
    private final Map<Integer, Pair<Integer, Integer>> monthChangeCountMap = NewUtil.treeMap();
    private final Map<Double, Integer> newMonthChangeCountMap = NewUtil.treeMap();

    AnalyzeDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
    }

    // ---------- private ----------

    /**
     * 统计最近三个月的月度变化占比
     *
     * @param bean
     */
    private void statisticsLastThreeMonthChangeCount(FundBean bean) {

    }

    /**
     * 统计最新一个月的月度变化占比
     *
     * @param bean
     */
    public void statisticsNewMonthChangeCount(FundBean bean) {

    }

    // ---------- get ----------


    public Map<Double, Integer> getNewMonthChangeCountMap() {
        return newMonthChangeCountMap;
    }

    public Map<Integer, Pair<Integer, Integer>> getMonthChangeCountMap() {
        return monthChangeCountMap;
    }
}
