package fund;

import fund.bean.FundBean;
import utils.NewUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FundHandlerContext {

    private final AtomicInteger FINISH_COUNTER = new AtomicInteger(0);

    private final boolean writeCsv;
    private final boolean writeExcel;

    private final int allIdCount;
    private final String date;
    private final String path;
    private final boolean needReserve;

    private final List<FundBean> beanList;

    private FundHandlerContext(Builder builder) {
        this.beanList = builder.beanList;
        this.allIdCount = builder.allIdCount;
        this.writeCsv = builder.writeCsv;
        this.writeExcel = builder.writeExcel;
        this.needReserve = builder.needReserve;
        this.date = builder.date;
        this.path = builder.path;
    }

    public AtomicInteger getFinishCounter() {
        return FINISH_COUNTER;
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

    @Override
    public String toString() {
        return "FundHandlerContext{" +
                "FINISH_COUNTER=" + FINISH_COUNTER +
                ", beanList=" + beanList +
                ", allIdCount=" + allIdCount +
                ", writeCsv=" + writeCsv +
                ", writeExcel=" + writeExcel +
                ", needReserve=" + needReserve +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
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
