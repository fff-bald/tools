package fund.handler;

import fund.bean.FundBean;

/**
 * 所有流程走完打个完成标记
 */
public class FinishHandler extends AbstractFundHandler {
    FinishHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
    }
}
