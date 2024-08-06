package fund.handler;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import fund.utils.FundDataBaseUtil;
import fund.utils.FundDataUtil;
import fund.utils.FundUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.ExceptionUtil;
import utils.LogUtil;
import utils.TimeUtil;

import java.io.IOException;

import static fund.constant.FundConstant.*;

/**
 * 获取每日数据
 */
public class GetFundDayDataHandler extends AbstractFundBeanHandler {
    GetFundDayDataHandler(int id) {
        super(id);
    }

    @Override
    public void doHandler(FundBean bean) {
        updateFundDayChangeFromWeb(bean);
    }

    // ---------- private ----------

    /**
     * 按照基金信息，获取基金每日数据
     *
     * @param bean
     */
    private void updateFundDayChangeFromWeb(FundBean bean) {
        try {
            int limit = Integer.MAX_VALUE;
            for (int i = 1; i <= limit; i++) {
                // 构建url
                String finalUrl = String.format(FUND_DAY_CHANGE_URL, bean.getId(),
                        START_DATE, bean.getUpdateTime().format(TimeUtil.YYYY_MM_DD_DTF), i);
                Document document = Jsoup.connect(finalUrl).get();

                // 解析数据
                for (Element tr : document.select("tbody").select("tr")) {
                    Elements td = tr.select("td");
                    FundDayBean fundDayBean = FundDayBean.valueOf(bean.getId(), td.get(0).text(), td.get(1).text(), td.get(2).text(), td.get(3).text(), td.get(4).text(), td.get(5).text());
                    if (FundDataUtil.checkExist(fundDayBean, bean.getDayBeanList())) {
                        break;
                    }
                    bean.getDayBeanList().add(fundDayBean);
                    FundDataBaseUtil.addData(fundDayBean, false);
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
