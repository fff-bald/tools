package funddata;

import funddata.bean.FundDataBean;
import funddata.bean.FundDataDayBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.StringUtil;
import utils.TimeUtil;

import java.io.IOException;
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
        // 从网络获取基金信息
        updateFundDataFromWeb(fundDataBean);

        String type = fundDataBean.getType();
        if (StringUtil.isBlank(type) || type.contains(INGORE_FUND_TYPE)) {
            return fundDataBean;
        }

        // 从网络获取每日数据
        updateFundDayChangeFromWeb(fundDataBean, TimeUtil.YYYY_MM_DD_SDF.format(new Date()));
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
            String m = tbody.select("td").get(1).text();
            bean.setMoney(m.substring(m.indexOf("：") + 1, m.indexOf("（")));
            bean.setManager(tbody.select("a").get(2).text());
        } catch (IndexOutOfBoundsException ioE) {
            System.out.println(String.format("【%s】【updateFundDataFromWeb】响应内容长度：%s", bean.getId(), document.text().length()));
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
                    FundDataDayBean fundDataDayBean = FundDataDayBean.valueOf(td.get(0).text(), td.get(1).text(), td.get(2).text(), td.get(3).text(), td.get(4).text(), td.get(5).text());
                    bean.getDayBeanList().add(fundDataDayBean);
                }

                // 当读取的是第一页时，有额外数据需要获取
                if (i == 1) {
                    limit = FundDataCollationUtil.getPagesValue(document.html());
                }
            }
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
        FundDataDayBean endDay = dayList.get(0);
        FundDataDayBean startDay = dayList.get(dayList.size() - 1);
        long totalDay = TimeUtil.calYearBetween(startDay.getDate(), endDay.getDate()) + 1;
        bean.setDurationDay((int) totalDay);
        int tradingDay = dayList.size();

        bean.setBuyState(endDay.getBuyState());
        bean.setSellState(endDay.getSellState());

        // 年化收益率
        bean.setYearChange(100 * FundDataCollationUtil.calYearChange(totalDay, startDay.getAllPrize(), endDay.getAllPrize()));

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
        for (int i = 0; i < dayBeanList.size(); i++) {
            FundDataDayBean dayBean = dayBeanList.get(i);
            // 1、尝试补足累计净值
            if (dayBean.getAllPrize() == 0 && i + 1 < dayBeanList.size()) {
                FundDataDayBean preDayBean = dayBeanList.get(i + 1);
                if (preDayBean == null) {
                    dayBean.setAllPrize(preDayBean.getAllPrize() * (1 + dayBean.getChange()));
                }
            } else if (dayBean.getAllPrize() == 0) {
                System.out.println(String.format("【%s】尝试补足累计净值失败，%s", bean.getId(), dayBean));
            }
            // 2、尝试补足当天变化值
            if (dayBean.getChange() == 0 && i + 1 < dayBeanList.size()) {
                FundDataDayBean preDayBean = dayBeanList.get(i + 1);
                dayBean.setChange((dayBean.getAllPrize() - preDayBean.getAllPrize()) / preDayBean.getAllPrize());
            }
        }
    }
}
