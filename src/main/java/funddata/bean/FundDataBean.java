package funddata.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * 基金数据
 *
 * @author cjl
 * @since 2024/7/4 22:19
 */
public class FundDataBean {
    /**
     * 基金id
     */
    private String id;

    /**
     * 名字
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 基金每日数据，日期新的放在前面
     */
    private List<FundDataDayBean> dayBeanList = new LinkedList<>();

    // ---------- 计算值 ----------

    /**
     * 最新一日申购状态
     */
    private String buyState;

    /**
     * 最新一日赎回状态
     */
    private String sellState;

    public static FundDataBean valueOf(String id, String name, String type) {
        FundDataBean res = new FundDataBean();
        res.id = id;
        res.name = name;
        res.type = type;
        return res;
    }

    public void cal() {
        FundDataDayBean curDay = this.dayBeanList.get(0);
        this.buyState = curDay.getBuyState();
        this.sellState = curDay.getSellState();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FundDataDayBean> getDayBeanList() {
        return dayBeanList;
    }

    public void setDayBeanList(List<FundDataDayBean> dayBeanList) {
        this.dayBeanList = dayBeanList;
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
        return "FundDataBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dayBeanList=" + dayBeanList +
                ", buyState='" + buyState + '\'' +
                ", sellState='" + sellState + '\'' +
                '}';
    }
}
