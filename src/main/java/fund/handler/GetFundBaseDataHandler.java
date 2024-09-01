package fund.handler;

import fund.bean.FundBean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import utils.ExceptionUtil;
import utils.JsoupUtil;
import utils.LogUtil;
import utils.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fund.constant.FundConstant.*;

/**
 * 爬取基金基础信息
 */
public class GetFundBaseDataHandler extends AbstractFundHandler {

    private static final String IGNORE_FUND_TYPE = "货币型-普通货币";
    private static final Pattern PERSON_PATTERN = Pattern.compile("个人投资者持有(\\d+\\.\\d+)亿份，占总份额的(\\d+\\.\\d+%)");

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

        String html = document.html();

        return true;
    }
}
