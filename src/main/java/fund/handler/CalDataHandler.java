package fund.handler;

import fund.bean.FundBean;
import fund.utils.FundCalUtil;

/**
 * 参数计算处理器
 */
public class CalDataHandler extends AbstractFundBeanHandler {
    CalDataHandler(int id) {
        super(id);
    }

    @Override
    public void doHandler(FundBean bean) {
        FundCalUtil.calFundData(bean);
    }
}
