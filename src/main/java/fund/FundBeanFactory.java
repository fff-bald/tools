package fund;

import fund.bean.FundBean;
import fund.handler.AbstractFundBeanHandler;
import fund.handler.FundBeanHandlerEnum;
import utils.TimeUtil;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author cjl
 * @date 2024/7/5 10:03
 */
public class FundBeanFactory {

    /**
     * 单例
     */
    private static final FundBeanFactory instance = new FundBeanFactory();


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

        for (FundBeanHandlerEnum handlerEnum : FundBeanHandlerEnum.values()) {
            AbstractFundBeanHandler handler = handlerEnum.getHandler();
            handler.doHandler(fundDataBean);
            if (!handler.isFinish(fundDataBean)) {
                return fundDataBean;
            }
        }

        return fundDataBean;
    }
}
