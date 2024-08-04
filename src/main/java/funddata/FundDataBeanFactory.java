package funddata;

import funddata.bean.FundDataBean;
import funddata.bean.FundDataDayBean;
import funddata.utils.FundCalUtil;
import funddata.utils.FundDataBaseUtil;
import funddata.utils.FundUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.HtmlUtil;
import utils.LogUtil;
import utils.StringUtil;
import utils.TimeUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static funddata.constant.FundDataConstant.*;

/**
 * @author cjl
 * @date 2024/7/5 10:03
 */
public class FundDataBeanFactory {

    /**
     * 单例
     */
    private static final FundDataBeanFactory instance = new FundDataBeanFactory();

    /**
     * 不爬货币基金数据
     */
    private static final String INGORE_FUND_TYPE = "货币型-普通货币";

    private FundDataBeanFactory() {

    }

    public static FundDataBeanFactory getInstance() {
        return instance;
    }

    public FundDataBean createBean(String id) {
        FundDataBean fundDataBean = FundDataBean.valueOf(id);
        // 1、从网络获取基金信息
        updateFundDataFromWeb(fundDataBean);

        String type = fundDataBean.getType();
        if (StringUtil.isBlank(type) || type.contains(INGORE_FUND_TYPE)) {
            return fundDataBean;
        }

        // 2、从网络获取每日数据
        updateFundDayChangeFromWeb(fundDataBean, TimeUtil.YYYY_MM_DD_SDF.format(new Date()));

        // 3、计算格式参数
        calFundData(fundDataBean);
        return fundDataBean;
    }

    public FundDataBean createBean(String id, String time) {
        FundDataBean fundDataBean = FundDataBean.valueOf(id);
        updateFundDayChangeFromWeb(fundDataBean, time);
        updateFundDataFromWeb(fundDataBean);
        calFundData(fundDataBean);
        return fundDataBean;
    }

    // ---------- private method ----------

    /**
     * 根据基金id，获取基金信息
     *
     * @param bean
     */
    private void updateFundDataFromWeb(FundDataBean bean) {
        Document document = null;
        try {
            // 构建url
            String url = String.format(FUND_DATA_GET_URL, bean.getId());
            document = Jsoup.connect(url).get();
            bean.setName(document.select("span.funCur-FundName").get(0).text());

            Element tbody = document.select("tbody").get(2);
            bean.setType(tbody.select("a").get(0).text());

            String money = tbody.select("td").get(1).text();
            bean.setMoney(money.substring(money.indexOf("：") + 1, money.indexOf("（")));
            bean.setManager(tbody.select("a").get(2).text());

            String lockTime = null;
            if (tbody.text().contains("封闭期")) {
                lockTime = HtmlUtil.findElement(tbody.children(), "封闭期").text();
            }
            bean.setLockTime(StringUtil.isBlank(lockTime) ? "无封闭期" : lockTime.substring(lockTime.indexOf("：") + 1));
        } catch (IndexOutOfBoundsException ioE) {
            LogUtil.info(LOG_NAME, "【%s】【updateFundDataFromWeb】响应内容长度：%s，可能原因：该ID基金不存在数据", bean.getId(), document.text().length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照基金信息，获取基金每日数据
     *
     * @param bean
     */
    private void updateFundDayChangeFromWeb(FundDataBean bean, String now) {
        try {
            int limit = Integer.MAX_VALUE;
            for (int i = 1; i <= limit; i++) {
                // 构建url
                String finalUrl = String.format(FUND_DAY_CHANGE_URL, bean.getId(), startDate, now, i);
                Document document = Jsoup.connect(finalUrl).get();

                // 解析数据
                for (Element tr : document.select("tbody").select("tr")) {
                    Elements td = tr.select("td");
                    FundDataDayBean fundDataDayBean = FundDataDayBean.valueOf(bean.getId(), td.get(0).text(), td.get(1).text(), td.get(2).text(), td.get(3).text(), td.get(4).text(), td.get(5).text());
                    if (FundDataBaseUtil.checkExist(fundDataDayBean)) {
                        break;
                    }
                    bean.getDayBeanList().add(fundDataDayBean);
                    FundDataBaseUtil.add(fundDataDayBean, false);
                }

                // 当读取的是第一页时，有额外数据需要获取
                if (i == 1) {
                    limit = FundUtil.getPagesValue(document.html());
                }
            }
            Collections.sort(bean.getDayBeanList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据已有的信息，计算各项指标
     *
     * @param bean
     */
    private void calFundData(FundDataBean bean) {
        // 数据清洗
        dataClean(bean);

        List<FundDataDayBean> dayList = bean.getDayBeanList();
        Collections.sort(dayList);

        FundDataDayBean endDay = dayList.get(0);
        FundDataDayBean startDay = dayList.get(dayList.size() - 1);

        // 设置基金存续时间
        long totalDay = TimeUtil.calYearBetween(startDay.getDate(), endDay.getDate()) + 1;
        bean.setDurationDay((int) totalDay);
        int tradingDay = dayList.size();

        // 设置最新一日申购状态和赎回状态
        bean.setBuyState(endDay.getBuyState());
        bean.setSellState(endDay.getSellState());

        // 设置复利年化收益率
        bean.setYearChangePro(100 * FundUtil.calYearChange(totalDay, startDay.getAllPrize(), endDay.getAllPrize()));
        // 设置年化收益率
        bean.setYearChange(100 * (endDay.getAllPrize() - startDay.getAllPrize()) / startDay.getAllPrize() / (totalDay / 365));

        // 设置三年期年化收益率
        FundCalUtil.setThreeYearChange(bean, dayList, new Date());

        // 设置历史最大回撤
        bean.setReduceRate(FundCalUtil.calMostReduceRate(dayList));

        // 上升比例
        double upDay = 0;
        double changeSum = 0;
        for (FundDataDayBean dayBean : bean.getDayBeanList()) {
            if (dayBean.getChange() >= 0) {
                upDay++;
            }
            changeSum += dayBean.getChange();
        }
        bean.setUpDayRate(100 * upDay / tradingDay);

        // 标准差
        double standardDeviation = 0;
        double changeAverage = changeSum / tradingDay;
        for (FundDataDayBean dayBean : bean.getDayBeanList()) {
            standardDeviation += Math.pow(dayBean.getChange() - changeAverage, 2);
        }
        bean.setDayStandardDeviation(Math.sqrt(standardDeviation / (tradingDay - 1)));
    }

    /**
     * 按照一定的策略，对数据进行整理
     *
     * @param bean
     */
    private void dataClean(FundDataBean bean) {
        List<FundDataDayBean> dayBeanList = bean.getDayBeanList();

        if (dayBeanList.isEmpty()) {
            LogUtil.error(LOG_NAME, "【%s】基金每日数据为空，%s", bean.getId(), bean);
            return;
        }

        // 第一天如果也没有数据可以直接设置为1
        FundDataDayBean startDay = dayBeanList.get(dayBeanList.size() - 1);
        if (startDay.getAllPrize() == Double.MIN_VALUE) {
            startDay.setAllPrize(1);
        }
        if (startDay.getPrice() == Double.MIN_VALUE) {
            startDay.setPrice(1);
        }
        if (startDay.getChange() == Double.MIN_VALUE) {
            startDay.setChange(0);
        }

        for (int i = dayBeanList.size() - 1; i >= 0; i--) {
            FundDataDayBean dayBean = dayBeanList.get(i);

            // 尝试修复累计净值数据
            if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                boolean isSuccess = false;
                // 1、尝试用今天的变化值和昨天的累积净值修复数据
                if (dayBean.getChange() != Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                    FundDataDayBean preDayBean = dayBeanList.get(i + 1);
                    if (preDayBean.getAllPrize() != Double.MIN_VALUE) {
                        dayBean.setAllPrize(preDayBean.getAllPrize() * (1 + dayBean.getChange()));
                        isSuccess = true;
                    }
                }

                // 2、尝试用明天的累积净值和变化值修复数据
                FundDataDayBean lastDayBean = dayBeanList.get(i - 1);
                if (lastDayBean.getChange() != Double.MIN_VALUE && lastDayBean.getAllPrize() != Double.MIN_VALUE) {
                    isSuccess = true;
                    dayBean.setAllPrize(lastDayBean.getAllPrize() / (1 + lastDayBean.getChange()));
                }
            }

            // 尝试修复当天变化值
            if (dayBean.getChange() == Double.MIN_VALUE && i + 1 < dayBeanList.size()) {
                FundDataDayBean preDayBean = dayBeanList.get(i + 1);
                if (dayBean.getAllPrize() != Double.MIN_VALUE && preDayBean.getAllPrize() != Double.MIN_VALUE) {
                    dayBean.setChange((dayBean.getAllPrize() - preDayBean.getAllPrize()) / preDayBean.getAllPrize());
                }
            }

            // ---------- 输出处理过还不合法的数据 ----------
            if (dayBean.getAllPrize() == Double.MIN_VALUE) {
                LogUtil.error(LOG_NAME, "【%s】累计净值异常，%s", bean.getId(), dayBean);
            }
        }
    }
}
