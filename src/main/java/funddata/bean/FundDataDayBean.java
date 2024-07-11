package funddata.bean;

import utils.StringUtil;

/**
 * 基金单天数据
 *
 * @author cjl
 * @since 2024/7/4 23:28
 */
public class FundDataDayBean {

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

    public static FundDataDayBean valueOf(String date, String p, String ap, String c, String buyState, String sellState) {
        FundDataDayBean res = new FundDataDayBean();
        res.date = StringUtil.isBlank(date) ? "" : date;
        res.price = StringUtil.isBlank(p) ? 0 : Double.parseDouble(p);
        res.allPrize = StringUtil.isBlank(ap) ? 0 : Double.parseDouble(ap);
        res.change = StringUtil.isBlank(c) ? 0 : Double.parseDouble(c.substring(0, c.lastIndexOf("%")));
        res.buyState = buyState;
        res.sellState = sellState;
        return res;
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

    @Override
    public String toString() {
        return "FundDataDayBean{" +
                "date='" + date + '\'' +
                ", price=" + price +
                ", allPrize=" + allPrize +
                ", change=" + change +
                ", buyState='" + buyState + '\'' +
                ", sellState='" + sellState + '\'' +
                '}';
    }
}
