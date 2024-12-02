package process.fund.handler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import process.fund.bean.FundBean;
import utils.*;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static process.fund.constant.FundConstant.FUND_DATA_GET_URL;
import static process.fund.constant.FundConstant.OCCUPY_PROPORTION_URL;

/**
 * 爬取基金基础信息
 */
public class GetFundBaseDataHandler extends AbstractFundHandler {

    private static final String IGNORE_FUND_TYPE = "货币型-普通货币";
    private static final Pattern PERSON_PATTERN = Pattern.compile("个人投资者持有(\\d+\\.\\d+)亿份，占总份额的(\\d+\\.\\d+%)");
    private static final Pattern RISK_PATTERN = Pattern.compile(".*\\|\\s*(中高风险|中低风险|低风险|高风险|中风险)\\s*");

    GetFundBaseDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        updateFundDataFromWeb(bean);
        updateFundTakeDataFromWeb(bean);
    }

    @Override
    public void doAfter(FundBean bean) {
        String type = bean.getType();
        if (StringUtil.isBlank(type)) {
            return;
        }
        // 不爬货币基金数据
        if (type.contains(IGNORE_FUND_TYPE)) {
            bean.setFailReason("该基金为货币基金");
            return;
        }

        super.doAfter(bean);
    }

    // ---------- private ----------

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
            document = JsoupUtil.getDocumentThrow(url);
            if (!checkFundHtml(document, bean)) {
                return;
            }

            bean.setName(document.select("span.funCur-FundName").get(0).text());

            Element tbody = document.select("tbody").get(2);
            bean.setType(tbody.select("a").get(0).text());

            Element riskElement = tbody.select("td:contains(风险)").first();
            if (riskElement != null) {
                String text = riskElement.text();
                Matcher matcher = RISK_PATTERN.matcher(text);
                if (matcher.matches()) {
                    // 输出匹配到的风险等级
                    String riskLevel = matcher.group(1);
                    bean.setRiskLevel(riskLevel);
                } else {
                    bean.setRiskLevel("未知风险");
                }
            }

            String money = tbody.select("td").get(1).text();
            bean.setMoney(money.substring(money.indexOf("：") + 1, money.indexOf("（") - 2));
            bean.setManager(tbody.select("a").get(2).text());

            String lockTime = null;
            if (tbody.text().contains("封闭期")) {
                lockTime = JsoupUtil.findElement(tbody.children(), "封闭期").text();
            }
            bean.setLockTime(StringUtil.isBlank(lockTime) ? "无封闭期" : lockTime.substring(lockTime.indexOf("：") + 1));
        } catch (IndexOutOfBoundsException ioE) {
            bean.setFailReason(String.format("【updateFundDataFromWeb】响应内容长度：%s，可能原因：该ID基金不存在数据"
                    , document.text().length()));
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    /**
     * 根据基金id，获取基金持有人占比相關信息
     *
     * @param bean
     */
    private void updateFundTakeDataFromWeb(FundBean bean) {
        Document document = null;
        try {
            // 构建url
            String url = String.format(OCCUPY_PROPORTION_URL, bean.getId());
            document = JsoupUtil.getDocumentThrow(url);

            String text = document.text();
            Matcher matcher = PERSON_PATTERN.matcher(text);

            if (matcher.find()) {
                // 提取匹配的数字
                // String holdingShares = matcher.group(1); // 总份额
                String holdingPercentage = matcher.group(2); // 持有比例
                bean.setPersonRate(holdingPercentage);
            } else {
                bean.setPersonRate("未匹配成功");
            }

        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    /**
     * 检查html网页数据
     *
     * @param document
     * @return
     */
    private boolean checkFundHtml(Document document, FundBean bean) {

        final String attributeKey = "data-date";

        // 尝试获取最近更新时间
        Elements divElements = document.select("div.titleItems.tabBtn.titleItemActive");
        // 获取data-date属性的值
        Element elementWithAttr = JsoupUtil.findElementWithAttr(divElements, attributeKey);
        if (elementWithAttr != null) {
            String dataDate = elementWithAttr.attr(attributeKey);
            if (StringUtil.isBlank(dataDate)) {
                bean.setFailReason("最新更新数据为空");
                return false;
            }
            LocalDate markDate = DateUtil.stringToLocalDate(dataDate).plusDays(30);
            LocalDate curDate = bean.getUpdateTime();
            if (markDate.isBefore(curDate)) {
                bean.setFailReason("距离上次更新时间大于30天");
                return false;
            }
        }

        return true;
    }
}
