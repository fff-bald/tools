package fund.handler;

import fund.bean.FundBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import utils.ExceptionUtil;
import utils.HtmlUtil;
import utils.LogUtil;
import utils.StringUtil;

import java.io.IOException;

import static fund.constant.FundConstant.FUND_DATA_GET_URL;
import static fund.constant.FundConstant.LOG_NAME;

public class GetFundBaseDataHandler extends AbstractFundBeanHandler {

    /**
     * 不爬货币基金数据
     */
    private static final String IGNORE_FUND_TYPE = "货币型-普通货币";

    GetFundBaseDataHandler(int id) {
        super(id);
    }

    @Override
    public void doHandler(FundBean bean) {
        updateFundDataFromWeb(bean);
    }

    @Override
    public boolean isFinish(FundBean bean) {
        if (!super.isFinish(bean)) {
            return false;
        }

        String type = bean.getType();
        boolean isIgnore = StringUtil.isBlank(type) || type.contains(IGNORE_FUND_TYPE);
        if (isIgnore) {
            LogUtil.info(LOG_NAME, "【%s】GetFundBaseDataHandler跳过，原因：isIgnore", bean.getId());
            bean.setState(FundBeanHandlerEnum.FINISH.getId());
            return false;
        }

        return true;
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
}
