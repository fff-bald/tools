package process.fund.model;

import com.alibaba.excel.annotation.ExcelProperty;
import process.fund.bean.FundBean;

import static process.fund.constant.FundConstant.EXCEL_FUND_LINK;

public class FundDataExcelModel {

    // ---------- 基础信息 ----------
    @ExcelProperty(value = "基金id")
    private String id;

    @ExcelProperty(value = "名字")
    private String name;

    @ExcelProperty(value = "类型")
    private String type;

    @ExcelProperty(value = "管理人")
    private String manager;

    @ExcelProperty(value = "规模(亿元)")
    private double money;

    @ExcelProperty(value = "封闭期")
    private String lockTime;

    // ---------- 计算值 ----------
    @ExcelProperty(value = "时间(年)")
    private double durationDay;

    @ExcelProperty(value = "上涨日数比例")
    private double upDayRate;

    @ExcelProperty(value = "上涨月份比例")
    private double upMonthRate;

    @ExcelProperty(value = "复利年化收益率(百分比)")
    private double yearChangePro;

    @ExcelProperty(value = "最大回撤")
    private double mostReduceRate;

    @ExcelProperty(value = "五年内最大回撤")
    private double fiveYearMostReduceRate;

    @ExcelProperty(value = "每月涨跌幅的最大值对比中值的倍数")
    private double monthMostChangeToAvg;

    @ExcelProperty(value = "近7天收益率(百分比)")
    private double sevenDayChange;

    @ExcelProperty(value = "近一月收益率(百分比)")
    private double monthChange;

    @ExcelProperty(value = "近三月收益率(百分比)")
    private double threeMonthChange;

    @ExcelProperty(value = "近六月收益率(百分比)")
    private double sixMonthChange;

    @ExcelProperty(value = "近一年收益率(百分比)")
    private double yearChange;

    @ExcelProperty(value = "近三年收益率(百分比)")
    private double threeYearChange;

    @ExcelProperty(value = "日涨跌幅标准差")
    private double dayStandardDeviation;

    @ExcelProperty(value = "月涨跌幅标准差")
    private double monthStandardDeviation;

    @ExcelProperty(value = "最新個人投資者份額占比")
    private String personRate;

    @ExcelProperty(value = "最新一日时间")
    private String updateTime;

    @ExcelProperty(value = "最新一日申购状态")
    private String buyState;

    @ExcelProperty(value = "最新一日赎回状态")
    private String sellState;

    // ---------- 不那么重要的 ----------

    public static FundDataExcelModel valueOf(FundBean bean) {
        FundDataExcelModel res = new FundDataExcelModel();
        // 将id接入Excel超链接
        res.id = String.format(EXCEL_FUND_LINK, bean.getId(), bean.getId());
        res.name = bean.getName();
        res.type = bean.getType();
        res.manager = bean.getManager();
        res.money = "--".equals(bean.getMoney()) ? 0 : Double.parseDouble(bean.getMoney());
        res.lockTime = bean.getLockTime();
        res.durationDay = bean.getDurationDay();
        res.upDayRate = bean.getUpDayRate();
        res.upMonthRate = bean.getUpMonthRate();
        res.yearChangePro = bean.getYearChangePro();
        res.mostReduceRate = bean.getMostReduceRate();
        res.fiveYearMostReduceRate = bean.getFiveYearMostReduceRate();
        res.monthMostChangeToAvg = bean.getMonthMostChangeToAvg();
        res.sevenDayChange = bean.getSevenDayChange();
        res.monthChange = bean.getMonthChange();
        res.threeMonthChange = bean.getThreeMonthChange();
        res.sixMonthChange = bean.getSixMonthChange();
        res.yearChange = bean.getYearChange();
        res.threeYearChange = bean.getThreeYearChange();
        res.dayStandardDeviation = bean.getDayStandardDeviation();
        res.monthStandardDeviation = bean.getMonthStandardDeviation();
        res.personRate = bean.getPersonRate();
        res.updateTime = bean.getUpdateTime().toString();
        res.buyState = bean.getBuyState();
        res.sellState = bean.getSellState();
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

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public double getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(double durationDay) {
        this.durationDay = durationDay;
    }

    public double getUpDayRate() {
        return upDayRate;
    }

    public void setUpDayRate(double upDayRate) {
        this.upDayRate = upDayRate;
    }

    public double getUpMonthRate() {
        return upMonthRate;
    }

    public void setUpMonthRate(double upMonthRate) {
        this.upMonthRate = upMonthRate;
    }

    public double getYearChangePro() {
        return yearChangePro;
    }

    public void setYearChangePro(double yearChangePro) {
        this.yearChangePro = yearChangePro;
    }

    public double getMostReduceRate() {
        return mostReduceRate;
    }

    public void setMostReduceRate(double mostReduceRate) {
        this.mostReduceRate = mostReduceRate;
    }

    public double getMonthMostChangeToAvg() {
        return monthMostChangeToAvg;
    }

    public void setMonthMostChangeToAvg(double monthMostChangeToAvg) {
        this.monthMostChangeToAvg = monthMostChangeToAvg;
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

    public double getYearChange() {
        return yearChange;
    }

    public void setYearChange(double yearChange) {
        this.yearChange = yearChange;
    }

    public double getThreeYearChange() {
        return threeYearChange;
    }

    public void setThreeYearChange(double threeYearChange) {
        this.threeYearChange = threeYearChange;
    }

    public double getDayStandardDeviation() {
        return dayStandardDeviation;
    }

    public void setDayStandardDeviation(double dayStandardDeviation) {
        this.dayStandardDeviation = dayStandardDeviation;
    }

    public double getMonthStandardDeviation() {
        return monthStandardDeviation;
    }

    public void setMonthStandardDeviation(double monthStandardDeviation) {
        this.monthStandardDeviation = monthStandardDeviation;
    }

    public String getPersonRate() {
        return personRate;
    }

    public void setPersonRate(String personRate) {
        this.personRate = personRate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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

    public double getFiveYearMostReduceRate() {
        return fiveYearMostReduceRate;
    }

    public void setFiveYearMostReduceRate(double fiveYearMostReduceRate) {
        this.fiveYearMostReduceRate = fiveYearMostReduceRate;
    }
}
