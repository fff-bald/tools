package process.fund;

import model.Pair;
import process.fund.bean.FundBean;
import utils.CollectionUtil;
import utils.DateUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FundHandlerContext {

    private final AtomicInteger finishCounter = new AtomicInteger(0);
    private final List<String> deleteIds = CollectionUtil.arraySycnList();

    /**
     * 每月统计 长债&中短债 基金数量
     * （年份*100+月份，（基金总数，月度收益为负的基金总数））
     */
    private final Map<Integer, Pair<Integer, Integer>> monthChangeCountMap;
    /**
     * 最近一个月，长债&中短债 基金收益分布
     */
    private final Map<Double, Integer> newMonthChangeCountMap;
    private final List<FundBean> beanList;

    /**
     * 长债&中短债数量
     */
    private final AtomicInteger statisticsFundCounter;
    /**
     * 长债&中短债 近一月产生最大回撤数量
     */
    private final AtomicInteger statisticsNewMonthMostReduceRateCounter;

    private final boolean writeCsv;
    private final boolean writeExcel;

    private final int allIdCount;
    private final String date;
    private final String path;
    private final boolean needReserve;

    private FundHandlerContext(Builder builder) {
        this.beanList = builder.beanList;
        this.allIdCount = builder.allIdCount;
        this.writeCsv = builder.writeCsv;
        this.writeExcel = builder.writeExcel;
        this.needReserve = builder.needReserve;
        this.date = builder.date;
        this.path = builder.path;
        this.newMonthChangeCountMap = builder.newMonthChangeCountMap;
        this.monthChangeCountMap = builder.monthChangeCountMap;
        this.statisticsFundCounter = builder.statisticsFundCounter;
        this.statisticsNewMonthMostReduceRateCounter = builder.statisticsNewMonthMostReduceRateCounter;
    }

    public AtomicInteger getFinishCounter() {
        return finishCounter;
    }

    public List<FundBean> getBeanList() {
        return beanList;
    }

    public int getAllIdCount() {
        return allIdCount;
    }

    public boolean isWriteCsv() {
        return writeCsv;
    }

    public boolean isWriteExcel() {
        return writeExcel;
    }

    public boolean isNeedReserve() {
        return needReserve;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public Map<Integer, Pair<Integer, Integer>> getMonthChangeCountMap() {
        return monthChangeCountMap;
    }

    public Map<Double, Integer> getNewMonthChangeCountMap() {
        return newMonthChangeCountMap;
    }

    public List<String> getDeleteIds() {
        return deleteIds;
    }

    public AtomicInteger getStatisticsFundCounter() {
        return statisticsFundCounter;
    }

    public AtomicInteger getStatisticsNewMonthMostReduceRateCounter() {
        return statisticsNewMonthMostReduceRateCounter;
    }

    @Override
    public String toString() {
        return "FundHandlerContext{" +
                "finishCounter=" + finishCounter +
                ", monthChangeCountMap=" + monthChangeCountMap +
                ", newMonthChangeCountMap=" + newMonthChangeCountMap +
                ", writeCsv=" + writeCsv +
                ", writeExcel=" + writeExcel +
                ", allIdCount=" + allIdCount +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                ", needReserve=" + needReserve +
                ", beanList=" + beanList +
                '}';
    }

    public static class Builder {
        private List<FundBean> beanList;
        private int allIdCount;
        private boolean writeCsv;
        private boolean writeExcel;
        private boolean needReserve;
        private String date = DateUtil.YYYY_MM_DD_SDF.format(DateUtil.getDate());
        private String path;
        private Map<Integer, Pair<Integer, Integer>> monthChangeCountMap;
        private Map<Double, Integer> newMonthChangeCountMap;
        private AtomicInteger statisticsFundCounter;
        private AtomicInteger statisticsNewMonthMostReduceRateCounter;

        public Builder setAllIdCount(int allIdCount) {
            this.allIdCount = allIdCount;
            return this;
        }

        public Builder setWriteCsv(boolean writeCsv) {
            this.writeCsv = writeCsv;
            return this;
        }

        public Builder setWriteExcel(boolean writeExcel) {
            this.writeExcel = writeExcel;
            if (writeExcel) {
                this.setNeedReserve(true);
            }
            return this;
        }

        public Builder setNeedReserve(boolean needReserve) {
            this.needReserve = needReserve;
            this.beanList = CollectionUtil.arraySycnList();
            this.monthChangeCountMap = CollectionUtil.treeMap();
            this.newMonthChangeCountMap = CollectionUtil.treeMap();
            this.statisticsFundCounter = new AtomicInteger(0);
            this.statisticsNewMonthMostReduceRateCounter = new AtomicInteger(0);
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public FundHandlerContext build() {
            return new FundHandlerContext(this);
        }
    }
}
