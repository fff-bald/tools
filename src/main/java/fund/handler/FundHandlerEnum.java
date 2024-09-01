package fund.handler;

public enum FundHandlerEnum {
    BASE_DATA(1000, new GetFundBaseDataHandler(1000)),

    DAY_DATA(2000, new GetFundDayDataHandler(2000)),

    CLEAN_DATA(3000, new CleanDataHandler(3000)),

    CAL_DATA(4000, new CalDataHandler(4000)),

    ANALYZE_DATA(5000, new AnalyzeHandler(5000)),

    FORMAT_DATA(6000, new FormatDataHandler(6000)),

    COMPRESS_DATA(7000, new CompressDataHandler(7000)),

    FINISH(Integer.MAX_VALUE, new FinishHandler(Integer.MAX_VALUE)),
    ;

    private final int id;
    private final AbstractFundHandler handler;

    FundHandlerEnum(int id, AbstractFundHandler handler) {
        this.id = id;
        this.handler = handler;
    }

    public int getId() {
        return id;
    }

    public AbstractFundHandler getHandler() {
        return handler;
    }
}
