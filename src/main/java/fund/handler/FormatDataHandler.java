package fund.handler;

import fund.bean.FundBean;
import utils.ReflectUtil;

/**
 * 按照一定要求，对Bean数据的格式进行处理
 */
public class FormatDataHandler extends AbstractFundHandler {
    FormatDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        ReflectUtil.formatDoubleField(bean, 2);
    }
}
