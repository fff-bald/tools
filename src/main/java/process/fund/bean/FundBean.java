package process.fund.bean;

import process.fund.utils.FundDataBaseUtil;
import tag.DescriptionField;
import utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * 基金数据
 *
 * @author cjl
 * @since 2024/7/4 22:19
 */
public class FundBean implements Comparable<FundBean> {

    // ---------- 基础信息 ----------
    @DescriptionField(value = "基金id")
    private String id;

    @DescriptionField(value = "名字")
    private String name;

    @DescriptionField(value = "类型")
    private String type;

    @DescriptionField(value = "管理人")
    private String manager;

    @DescriptionField(value = "规模(亿元)")
    private String money;

    @DescriptionField(value = "封闭期")
    private String lockTime;

    // ---------- 计算值 ----------
    @DescriptionField(value = "时间(年)")
    private double durationDay;

    @DescriptionField(value = "上涨日数比例")
    private double upDayRate;

    @DescriptionField(value = "上涨月份比例")
    private double upMonthRate;

    @DescriptionField(value = "复利年化收益率(百分比)")
    private double yearChangePro;

    @DescriptionField(value = "最大回撤")
    private double mostReduceRate;

    @DescriptionField(value = "每月涨跌幅的最大值对比中值的倍数")
    private double monthMostChangeToAvg;

    @DescriptionField(value = "近7天收益率(百分比)")
    private double sevenDayChange;

    @DescriptionField(value = "近一月收益率(百分比)")
    private double monthChange;

    @DescriptionField(value = "近三月收益率(百分比)")
    private double threeMonthChange;

    @DescriptionField(value = "近六月收益率(百分比)")
    private double sixMonthChange;

    @DescriptionField(value = "近一年收益率(百分比)")
    private double yearChange;

    @DescriptionField(value = "近三年收益率(百分比)")
    private double threeYearChange;

    @DescriptionField(value = "日涨跌幅标准差")
    private double dayStandardDeviation;

    @DescriptionField(value = "月涨跌幅标准差")
    private double monthStandardDeviation;

    @DescriptionField(value = "最新個人投資者份額占比")
    private String personRate;

    @DescriptionField(value = "最新一日时间")
    private LocalDate updateTime;

    @DescriptionField(value = "最新一日申购状态")
    private String buyState;

    @DescriptionField(value = "最新一日赎回状态")
    private String sellState;

    // ---------- 中间值 ----------

    /**
     * 基金每月数据，日期新的放在前面
     */
    private List<FundMonthBean> monthBeanList;

    /**
     * 基金每日数据，日期新的放在前面
     */
    private List<FundDayBean> dayBeanList;

    /**
     * 进展状态
     */
    private int state;

    /**
     * 任务失败原因，任务成功此处为null
     */
    private String failReason;

    /**
     * 交易日
     */
    private int tradeDay;

    /**
     * 月均变化比例
     */
    private double monthAvgChange;

    public static FundBean valueOf(String id) {
        return valueOf(id, DateUtil.getLocalDate());
    }

    public static FundBean valueOf(String id, LocalDate updateTime) {
        FundBean res = new FundBean();
        res.id = id;
        res.dayBeanList = FundDataBaseUtil.getDataBeforeDate(id, updateTime.format(DateUtil.YYYY_MM_DD_DTF));
        res.updateTime = updateTime;
        return res;
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

    public List<FundDayBean> getDayBeanList() {
        return dayBeanList;
    }

    public void setDayBeanList(List<FundDayBean> dayBeanList) {
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public double getYearChange() {
        return yearChange;
    }

    public void setYearChange(double yearChange) {
        this.yearChange = yearChange;
    }

    public double getUpDayRate() {
        return upDayRate;
    }

    public void setUpDayRate(double upDayRate) {
        this.upDayRate = upDayRate;
    }

    public double getDayStandardDeviation() {
        return dayStandardDeviation;
    }

    public void setDayStandardDeviation(double dayStandardDeviation) {
        this.dayStandardDeviation = dayStandardDeviation;
    }

    public double getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(double durationDay) {
        this.durationDay = durationDay;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public List<FundMonthBean> getMonthBeanList() {
        return monthBeanList;
    }

    public void setMonthBeanList(List<FundMonthBean> monthBeanList) {
        this.monthBeanList = monthBeanList;
    }

    public double getYearChangePro() {
        return yearChangePro;
    }

    public void setYearChangePro(double yearChangePro) {
        this.yearChangePro = yearChangePro;
    }

    public double getThreeYearChange() {
        return threeYearChange;
    }

    public void setThreeYearChange(double threeYearChange) {
        this.threeYearChange = threeYearChange;
    }

    public double getUpMonthRate() {
        return upMonthRate;
    }

    public void setUpMonthRate(double upMonthRate) {
        this.upMonthRate = upMonthRate;
    }

    public double getMostReduceRate() {
        return mostReduceRate;
    }

    public void setMostReduceRate(double mostReduceRate) {
        this.mostReduceRate = mostReduceRate;
    }

    public double getMonthStandardDeviation() {
        return monthStandardDeviation;
    }

    public void setMonthStandardDeviation(double monthStandardDeviation) {
        this.monthStandardDeviation = monthStandardDeviation;
    }

    public LocalDate getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDate updateTime) {
        this.updateTime = updateTime;
    }

    public double getMonthMostChangeToAvg() {
        return monthMostChangeToAvg;
    }

    public void setMonthMostChangeToAvg(double monthMostChangeToAvg) {
        this.monthMostChangeToAvg = monthMostChangeToAvg;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(int tradeDay) {
        this.tradeDay = tradeDay;
    }

    public double getSevenDayChange() {
        return sevenDayChange;
    }

    public void setSevenDayChange(double sevenDayChange) {
        this.sevenDayChange = sevenDayChange;
    }

    public double getMonthChange() {
        return monthChange;
    }

    public void setMonthChange(double monthChange) {
        this.monthChange = monthChange;
    }

    public double getThreeMonthChange() {
        return threeMonthChange;
    }

    public void setThreeMonthChange(double threeMonthChange) {
        this.threeMonthChange = threeMonthChange;
    }

    public double getSixMonthChange() {
        return sixMonthChange;
    }

    public void setSixMonthChange(double sixMonthChange) {
        this.sixMonthChange = sixMonthChange;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getPersonRate() {
        return personRate;
    }

    public void setPersonRate(String personRate) {
        this.personRate = personRate;
    }

    public double getMonthAvgChange() {
        return monthAvgChange;
    }

    public void setMonthAvgChange(double monthAvgChange) {
        this.monthAvgChange = monthAvgChange;
    }

    @Override
    public int compareTo(FundBean o) {
        // 字符串顺序，从小到大
        return this.getId().compareTo(o.getId());
    }
}
