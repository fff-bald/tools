package process.fund.bean;

/**
 * 基金月度数据
 *
 * @author cjl
 * @since 2024/8/4 13:29
 */
public class FundMonthBean implements Comparable<FundMonthBean> {

    private int year;

    private int month;

    private FundDayBean startDay;

    private FundDayBean endDay;

    private double change;

    public static FundMonthBean valueOf(int year, int month, FundDayBean startDay, FundDayBean endDay) {
        FundMonthBean res = new FundMonthBean();
        res.year = year;
        res.month = month;
        res.change = 100 * (endDay.getAllPrize() - startDay.getAllPrize()) / startDay.getAllPrize();
        res.startDay = startDay;
        res.endDay = endDay;
        return res;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public FundDayBean getStartDay() {
        return startDay;
    }

    public FundDayBean getEndDay() {
        return endDay;
    }

    public double getChange() {
        return change;
    }

    @Override
    public int compareTo(FundMonthBean o) {
        // 从大到小
        return (o.getYear() * 100 + o.getMonth()) - (this.getYear() * 100 + this.getMonth());
    }
}
