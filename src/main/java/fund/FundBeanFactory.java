package fund;

import fund.bean.FundBean;
import fund.handler.AbstractFundHandler;
import fund.handler.FundHandlerEnum;

import java.time.LocalDate;

/**
 * @author cjl
 * @date 2024/7/5 10:03
 */
public class FundBeanFactory {

    /**
     * 单例
     */
    private static final FundBeanFactory instance = new FundBeanFactory();

    /**
     * 上下文信息
     */
    private FundHandlerContext context;

    private FundBeanFactory() {
        FundHandlerContext.Builder builder = new FundHandlerContext.Builder();
        context = builder.build();
    }

    public static FundBeanFactory getInstance() {
        return instance;
    }

    public FundHandlerContext getInstanceContext() {
        return instance.context;
    }

    public void updateInstanceContext(FundHandlerContext context) {
        instance.context = context;
    }

    public FundBean createBean(String id) {
        return createBean(id, context.getDate());
    }

    public FundBean createBean(String id, String time) {
        FundBean fundDataBean = FundBean.valueOf(id, LocalDate.parse(time));

        for (FundHandlerEnum handlerEnum : FundHandlerEnum.values()) {
            AbstractFundHandler handler = handlerEnum.getHandler();
            handler.doHandler(fundDataBean);
            if (!handler.checkFinish(fundDataBean)) {
                return fundDataBean;
            }
        }

        return fundDataBean;
    }
}
