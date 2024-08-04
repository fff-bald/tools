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

    public static FundDataMonthBean valueOf() {
        FundDataMonthBean res = new FundDataMonthBean();
        return res;
    }
}
