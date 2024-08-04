package funddata.bean;

/**
 * 基金月度数据
 *
 * @author cjl
 * @since 2024/8/4 13:29
 */
public class FundDataMonthBean {

    private int year;

    private int month;

    private double change;

    public static FundDataMonthBean valueOf(int year, int month) {
        FundDataMonthBean res = new FundDataMonthBean();
        res.year = year;
        res.month = month;
        return res;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}
