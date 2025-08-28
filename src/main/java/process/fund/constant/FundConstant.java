package process.fund.constant;

import utils.ConfigUtil;

/**
 * 魔法值
 *
 * @author cjl
 * @date 2024/7/5 10:13
 */
public interface FundConstant {

    // ---------- STATE ----------

    boolean NEED_EMAIL = false;

    // --------- DATE ----------

    String START_DATE = "";

    // ---------- URL ----------

    // 全量基金ID数据存在一个js文件里
    String ALL_FUND_IDS_URL = "";

    // 基金资料获取链接模板
    String FUND_DATA_GET_URL = "";

    // 每日数据获取链接模板
    String FUND_DAY_CHANGE_URL = "";

    // 持有人占比获取链接模板
    String OCCUPY_PROPORTION_URL = "";

    // --------- FILE ----------

    String CSV_FILE_ABSOLUTE_PATH = "";

    String EXCEL_FILE_ABSOLUTE_PATH = "";

    // ---------- EXCEL ----------

    // Excel指向基金网页的超链接
    String EXCEL_FUND_LINK = "";

    // ---------- SHEET ----------

    String BOND_FUND_MONTH_ANALYZE = "1、债基月度分析";

    String ALL_DATA = "2、全量数据";

    String LONG_TIME_FUNDS = "3、长线稳健基金";

    // ---------- EMAIL ----------

    String RECEIVER_EMAIL_NAME = ConfigUtil.getInitConfig("email.common.receiver.address");

}