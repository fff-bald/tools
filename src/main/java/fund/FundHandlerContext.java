package fund;

import fund.bean.FundBean;
import model.Pair;
import utils.NewUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FundHandlerContext {

    private final AtomicInteger finishCount = new AtomicInteger(0);
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
    }

    public AtomicInteger getFinishCounter() {
        return finishCount;
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

    @Override
    public String toString() {
        return "FundHandlerContext{" +
                "finishCount=" + finishCount +
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
        private String date = TimeUtil.YYYY_MM_DD_SDF.format(new Date());
        private String path;
        private Map<Integer, Pair<Integer, Integer>> monthChangeCountMap;
        private Map<Double, Integer> newMonthChangeCountMap;

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
            this.beanList = NewUtil.arraySycnList();
            this.monthChangeCountMap = NewUtil.treeMap();
            this.newMonthChangeCountMap = NewUtil.treeMap();
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
