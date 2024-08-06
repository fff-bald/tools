package fund.handler;

import fund.bean.FundBean;
import fund.utils.FundDataUtil;

/**
 * 按照一定策略，对数据进行清洗
 */
public class CleanDataHandler extends AbstractFundBeanHandler {
    CleanDataHandler(int id) {
        super(id);
    }

    @Override
    public void doHandler(FundBean bean) {
        FundDataUtil.repairData(bean);
    }
}
