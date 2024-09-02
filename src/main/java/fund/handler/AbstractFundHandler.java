package fund.handler;

import fund.FundBeanFactory;
import fund.FundHandlerContext;
import fund.bean.FundBean;

/**
 * 处理器抽象类
 */
public abstract class AbstractFundHandler {

    private final int id;

    AbstractFundHandler(int id) {
        this.id = id;
    }

    public final boolean checkFinish(FundBean bean) {
        return this.id == bean.getState();
    }

    public final FundHandlerContext getContext() {
        return FundBeanFactory.getInstance().getInstanceContext();
    }

    public final void doHandler(FundBean bean) {
        doBefore(bean);
        doing(bean);
        doAfter(bean);
    }

    /**
     * 处理前操作
     *
     * @param bean
     */
    public void doBefore(FundBean bean) {

    }

    /**
     * 处理操作
     *
     * @param bean
     */
    public abstract void doing(FundBean bean);

    /**
     * 处理后操作
     *
     * @param bean
     */
    public void doAfter(FundBean bean) {

        if (bean.getFailReason() != null) {
            return;
        }

        bean.setState(getId());
    }

    // ---------- get ----------

    public int getId() {
        return id;
    }
}
