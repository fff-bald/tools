package fund.handler;

import fund.bean.FundBean;
import fund.bean.FundDayBean;
import fund.utils.FundDataBaseUtil;
import fund.utils.FundDataUtil;
import fund.utils.FundUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.*;

import java.util.List;
import java.util.Set;

import static fund.constant.FundConstant.FUND_DAY_CHANGE_URL;
import static fund.constant.FundConstant.START_DATE;

/**
 * 获取每日数据
 */
public class GetFundDayDataHandler extends AbstractFundBeanHandler {
    GetFundDayDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        updateFundDayChangeFromWeb(bean);
    }

    @Override
    public void doAfter(FundBean bean) {
        List<FundDayBean> dayBeanList = bean.getDayBeanList();

        if (bean.getTradeDay() == 0) {
            bean.setFailReason("没有每日数据，应该是新基金");
            return;
        }

        Set<String> onlySet = NewUtil.hashSet();
        for (FundDayBean dayBean : dayBeanList) {
            if (onlySet.contains(dayBean.getDate())) {
                bean.setFailReason(String.format("每日数据中存在重复数据，日期：%s", dayBean.getDate()));
                LogUtil.error("【{}】每日数据中存在重复数据，日期：{}", bean.getId()
                        , dayBean.getDate());
                return;
            }
            onlySet.add(dayBean.getDate());
        }

        if (dayBeanList.size() != bean.getTradeDay()) {
            bean.setFailReason("每日数据量和交易日天数不一致");
            LogUtil.error("【{}】每日数据是否大于交易日天数：{}", bean.getId()
                    , dayBeanList.size() > bean.getTradeDay());
            return;
        }

        FundDataBaseUtil.addDataList(dayBeanList);
        super.doAfter(bean);
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
                Document document = JsoupUtil.getDocumentThrow(finalUrl);

                // 当读取的是第一页时，有额外数据需要获取
                if (i == 1) {
                    limit = FundUtil.getPagesValue(document.html(), "pages:");
                    bean.setTradeDay(FundUtil.getPagesValue(document.html(), "records:"));
                    if (bean.getTradeDay() == 0) {
                        // 没有每日数据，应该是新基金
                        return;
                    }
                }

                // 解析数据
                for (Element tr : document.select("tbody").select("tr")) {
                    Elements td = tr.select("td");
                    FundDayBean fundDayBean = FundDayBean.valueOf(bean.getId(), td.get(0).text(), td.get(1).text(), td.get(2).text(), td.get(3).text(), td.get(4).text(), td.get(5).text());
                    if (FundDataUtil.checkExist(fundDayBean, bean.getDayBeanList())) {
                        // 说明这之前的数据都已经有了，直接返回
                        return;
                    }
                    bean.getDayBeanList().add(fundDayBean);
                }
            }
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
