package process.fund.handler;

import process.fund.bean.FundBean;
import tag.DescriptionField;
import utils.ExceptionUtil;
import utils.LogUtil;
import utils.ReflectUtil;

/**
 * 按照一定逻辑对Bean数据进行压缩
 */
public class CompressDataHandler extends AbstractFundHandler {
    CompressDataHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        try {
            // 将用不上字段的数据清一下
            ReflectUtil.resetFieldsWithout(bean, DescriptionField.class);
        } catch (IllegalAccessException e) {
            LogUtil.error("【{}】异常信息：{}", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }
}
