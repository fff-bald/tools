package fund.handler;

import fund.bean.FundBean;

/**
 * 所有流程走完打个完成标记
 */
public class FinishHandler extends AbstractFundBeanHandler {
    FinishHandler(int id) {
        super(id);
    }

    @Override
    public void doHandler(FundBean bean) {
        bean.setState(this.getId());
    }
}
