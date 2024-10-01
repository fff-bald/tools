package process.fund.bean;

import utils.StringUtil;

/**
 * 基金单天数据
 *
 * @author cjl
 * @since 2024/7/4 23:28
 */
public class FundDayBean implements Comparable<FundDayBean> {

    // ---------- 数据 ----------

    /**
     * id
     */
    private String id;


    /**
     * 当天日期
     */
    private String date;

    /**
     * 单位净值
     */
    private double price;

    /**
     * 累计净值
     */
    private double allPrize;

    /**
     * 当天变化值
     */
    private double change;

    /**
     * 申购状态
     */
    private String buyState;

    /**
     * 赎回状态
     */
    private String sellState;

    // ---------- 中间值 ----------

    /**
     * 是否已经持久化入库
     */
    private boolean isPersistence = false;

    public static FundDayBean valueOf(String id, String date, String p, String ap, String c, String buyState, String sellState) {
        FundDayBean res = new FundDayBean();
        res.id = id;
        res.date = StringUtil.isBlank(date) ? "" : date;
        res.price = StringUtil.isBlank(p) ? Double.MIN_VALUE : Double.parseDouble(p);
        res.allPrize = StringUtil.isBlank(ap) ? Double.MIN_VALUE : Double.parseDouble(ap);
        res.change = StringUtil.isBlank(c) ? Double.MIN_VALUE :
                Double.parseDouble(c.substring(0, c.lastIndexOf("%")));
        res.buyState = buyState;
        res.sellState = sellState;
        return res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAllPrize() {
        return allPrize;
    }

    public void setAllPrize(double allPrize) {
        this.allPrize = allPrize;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getBuyState() {
        return buyState;
    }

    public void setBuyState(String buyState) {
        this.buyState = buyState;
    }

    public String getSellState() {
        return sellState;
    }

    public void setSellState(String sellState) {
        this.sellState = sellState;
    }

    public boolean isPersistence() {
        return isPersistence;
    }

    public void setPersistence(boolean persistence) {
        isPersistence = persistence;
    }

    @Override
    public int compareTo(FundDayBean o) {
        // 字符串顺序，从大到小
        return o.getDate().compareTo(this.getDate());
    }

    @Override
    public String toString() {
        return "FundDayBean{" +
                "date='" + date + '\'' +
                ", price=" + price +
                ", allPrize=" + allPrize +
                ", change=" + change +
                '}';
    }
}
