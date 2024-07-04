package funddata;

import funddata.bean.FundDataBean;

import java.util.List;

/**
 * 基金数据爬取整理APP
 *
 * @author cjl
 * @since 2024/7/4.
 */
public class FundDataCollationApp {
    /**
     * 入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        List<FundDataBean> allIds = FundDataCollationUtil.getAllFundIdsFromWeb();
        FundDataCollationUtil.getFundDayChangeFromWeb(allIds.get(0));
        System.out.println(allIds.get(0));
    }
}
