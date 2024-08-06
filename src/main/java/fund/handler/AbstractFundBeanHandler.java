package fund.handler;

import fund.bean.FundBean;

public abstract class AbstractFundBeanHandler {

    private final int id;

    AbstractFundBeanHandler(int id) {
        this.id = id;
    }

    public abstract void doHandler(FundBean bean);

    public boolean isFinish(FundBean bean) {
        return true;
    }

    // ---------- get ----------

    public int getId() {
        return id;
    }
}
