package process.fund.handler;

import process.fund.FundHandlerContext;
import process.fund.bean.FundBean;
import tag.DescriptionField;
import utils.FileUtil;
import utils.ReflectUtil;

/**
 * 数据持久化处理器
 *
 * @author cjl
 * @date 2024/9/2 11:48
 */
public class WriteHandler extends AbstractFundHandler {
    WriteHandler(int id) {
        super(id);
    }

    @Override
    public void doing(FundBean bean) {
        FundHandlerContext context = getContext();

        if (context.isWriteCsv()) {
            FileUtil.writeStringToFile(context.getPath(), "'" +
                    ReflectUtil.getAllFieldValue(bean, DescriptionField.class, ","), true);
        }
    }
}
