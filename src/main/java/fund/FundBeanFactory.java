package fund;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import fund.utils.FundCalUtil;
import fund.utils.FundDataBaseUtil;
import fund.utils.FundDataUtil;
import fund.utils.FundUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import static fund.constant.FundConstant.*;

/**
 * @author cjl
 * @date 2024/7/5 10:03
 */
public class FundBeanFactory {

    /**
     * 单例
     */
    private static final FundBeanFactory instance = new FundBeanFactory();

    /**
     * 不爬货币基金数据
     */
    private static final String INGORE_FUND_TYPE = "货币型-普通货币";

    private FundBeanFactory() {
    }

    public static FundBeanFactory getInstance() {
        return instance;
    }

    public FundBean createBean(String id) {
        return createBean(id, TimeUtil.YYYY_MM_DD_SDF.format(new Date()));
    }

    public FundBean createBean(String id, String time) {
        FundBean fundDataBean = FundBean.valueOf(id, LocalDate.parse(time));

        // 1、从网络获取基金信息
        updateFundDataFromWeb(fundDataBean);
        String type = fundDataBean.getType();
        if (StringUtil.isBlank(type) || type.contains(INGORE_FUND_TYPE)) {
            return fundDataBean;
        }

        // 2、从网络获取每日数据
        updateFundDayChangeFromWeb(fundDataBean, time);

        // 3、按照一定策略，对数据进行清洗
        FundDataUtil.repairData(fundDataBean);

        // 4、计算格式参数
        FundCalUtil.calFundData(fundDataBean);
        return fundDataBean;
    }

    // ---------- private method ----------

    /**
     * 根据基金id，获取基金信息
     *
     * @param bean
     */
    private void updateFundDataFromWeb(FundBean bean) {
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
            LogUtil.error(LOG_NAME, "【%s】IOException，异常信息：%s", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    /**
     * 按照基金信息，获取基金每日数据
     *
     * @param bean
     */
    private void updateFundDayChangeFromWeb(FundBean bean, String now) {
        try {
            int limit = Integer.MAX_VALUE;
            for (int i = 1; i <= limit; i++) {
                // 构建url
                String finalUrl = String.format(FUND_DAY_CHANGE_URL, bean.getId(), START_DATE, now, i);
                Document document = Jsoup.connect(finalUrl).get();

                // 解析数据
                for (Element tr : document.select("tbody").select("tr")) {
                    Elements td = tr.select("td");
                    FundDayBean fundDayBean = FundDayBean.valueOf(bean.getId(), td.get(0).text(), td.get(1).text(), td.get(2).text(), td.get(3).text(), td.get(4).text(), td.get(5).text());
                    if (FundDataUtil.checkExist(fundDayBean, bean.getDayBeanList())) {
                        break;
                    }
                    bean.getDayBeanList().add(fundDayBean);
                    FundDataBaseUtil.add(fundDayBean, false);
                }

                // 当读取的是第一页时，有额外数据需要获取
                if (i == 1) {
                    limit = FundUtil.getPagesValue(document.html());
                }
            }
        } catch (IOException e) {
            LogUtil.error(LOG_NAME, "【%s】IOException，异常信息：%s", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
