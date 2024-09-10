package process.fund.handler;

import process.fund.FundHandlerContext;
import process.fund.bean.FundBean;

/**
 * 所有流程走完打个完成标记
 */
public class FinishHandler extends AbstractFundHandler {
    FinishHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        FundHandlerContext context = getContext();
        context.getFinishCounter().incrementAndGet();

        if (context.isNeedReserve()) {
            context.getBeanList().add(bean);
        }
    }
}
