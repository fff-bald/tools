package fund.constant;

/**
 * 魔法值
 *
 * @author cjl
 * @date 2024/7/5 10:13
 */
public interface FundConstant {

    String START_DATE = "1970-01-01";

    // 每日数据获取链接模板
    String FUND_DAY_CHANGE_URL = "https://for.example.com";

    // 持有人結構获取链接模板
    String OCCUPY_PROPORTION_URL = "https://for.example.com";

    // 全量基金ID数据存在一个js文件里
    String ALL_FUND_IDS_URL = "https://for.example.com";

    // 全量基金ID数据存在一个js文件里
    String FUND_DATA_GET_URL = "https://for.example.com";

    String FILE_ABSOLUTE_PATH = "C:\\FundData\\%s.csv";
}
