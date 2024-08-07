package fund.handler;

import fund.bean.FundBean;

public abstract class AbstractFundBeanHandler {

    private final int id;

    AbstractFundBeanHandler(int id) {
        this.id = id;
    }

    public final boolean checkFinish(FundBean bean) {
        return this.id == bean.getState();
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
     * 处理中操作
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
        bean.setState(getId());
    }

    // ---------- get ----------

    public int getId() {
        return id;
    }
}
