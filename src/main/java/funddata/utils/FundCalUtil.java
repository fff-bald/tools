package funddata.utils;

import funddata.bean.FundDataBean;
import funddata.bean.FundDataDayBean;
import utils.TimeUtil;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static utils.TimeUtil.YYYY_MM_DD_DTF;

/**
 * 数据计算工具类
 *
 * @author cjl
 * @since 2024/8/4 18:18
 */
public class FundCalUtil {

    /**
     * 设置三年的年化收益率，如果不够三年也按三年来算
     *
     * @param dayList
     * @param time
     * @return
     */
    public static void setThreeYearChange(FundDataBean bean, List<FundDataDayBean> dayList, Date time) {
        LocalDate today = TimeUtil.convertDateToLocalDate(time);
        LocalDate ago = today.minusYears(3);
        FundDataDayBean startDateBean = null;
        FundDataDayBean endDayBean = dayList.get(0);
        for (FundDataDayBean dayBean : dayList) {
            LocalDate localDate = LocalDate.parse(dayBean.getDate(), YYYY_MM_DD_DTF);
            if (ago.isBefore(localDate) || ago.isEqual(localDate)) {
                startDateBean = dayBean;
            } else {
                break;
            }
        }

        // 年化收益率
        bean.setThreeYearChange(100 * (endDayBean.getAllPrize() - startDateBean.getAllPrize()) / startDateBean.getAllPrize() / 3);
        // 复利年化收益率
        bean.setThreeYearChangePro(100 * FundUtil.calYearChange(3 * 365, startDateBean.getAllPrize(), endDayBean.getAllPrize()));
    }

    /**
     * 计算最大回撤
     *
     * @return
     */
    public static double calMostReduceRate(List<FundDataDayBean> netValues) {

        if (netValues == null || netValues.isEmpty() || netValues.size() == 1) {
            return 0.0;
        }

        double peak = netValues.get(0).getAllPrize(); // 初始峰值为列表的第一个值
        double maxDrawDown = 0.0; // 最大回撤初始化为0

        for (FundDataDayBean dayBean : netValues) {
            double netValue = dayBean.getAllPrize();
            if (netValue > peak) {
                // 如果当前净值高于之前的峰值，则更新峰值
                peak = netValue;
            } else {
                // 计算当前净值与峰值之间的回撤
                double drawDown = (peak - netValue) / peak;
                if (drawDown > maxDrawDown) {
                    // 如果当前回撤大于之前的最大回撤，则更新最大回撤
                    maxDrawDown = drawDown;
                }
            }
        }

        // 返回最大回撤的百分比（转换为百分比形式）
        return maxDrawDown * 100.0;
    }
}
