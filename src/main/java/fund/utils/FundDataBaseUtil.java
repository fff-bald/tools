package fund.utils;

import fund.bean.FundDayBean;
import utils.*;

import java.util.List;
import java.util.Set;

/**
 * 数据库工具类
 *
 * @author cjl
 * @since 2024/8/4 13:50
 */
public class FundDataBaseUtil {

    private static final String INIT_PATH = ".\\fund_data\\%s.txt";

    // ---------- public ----------

    /**
     * @param dayBeans
     * @param isAppend 是否为追加，不是即为覆盖
     */
    public static void addDataList(List<FundDayBean> dayBeans, boolean isAppend) {
        for (FundDayBean dayBean : dayBeans) {
            addData(dayBean, isAppend);
        }
    }

    /**
     * @param dataDayBean
     * @param isAppend    是否为追加，不是即为覆盖
     */
    public static void addData(FundDayBean dataDayBean, boolean isAppend) {
        if (checkExistInDataBase(dataDayBean)) {
            return;
        }

        String id = dataDayBean.getId();
        dataDayBean.setPersistence(true);
        try {
            FileUtil.writeStringToFile(getFilePath(id), JsonUtil.toJson(dataDayBean), isAppend);
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", dataDayBean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    public static List<FundDayBean> getData(String id) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = NewUtil.arrayList();
        for (String str : strings) {
            if (str.contains(id)) {
                try {
                    FundDayBean dayBean = JsonUtil.toObject(str, FundDayBean.class);
                    res.add(dayBean);
                } catch (Exception e) {
                    LogUtil.error("【{}】异常信息：{}", id, ExceptionUtil.getStackTraceAsString(e));
                }
            }
        }
        return res;
    }

    /**
     * 检查该数据是否已经在数据库中存在
     *
     * @param dataDayBean
     * @return
     */
    public static boolean checkExistInDataBase(FundDayBean dataDayBean) {
        return dataDayBean.isPersistence();
    }

    /**
     * 通过基金id将数据库里相关信息删除
     *
     * @param id
     */
    public static void clearFundDataInDataBasById(String id) {
        Set<String> set = NewUtil.hashSet();
        List<FundDayBean> data = getData(id);
        List<FundDayBean> res = NewUtil.arrayList(data.size());
        for (FundDayBean bean : data) {

            if (bean.getId().equals(id)) {
                continue;
            }

            String key = bean.getId() + bean.getDate();
            if (set.contains(key)) {
                continue;
            }

            set.add(key);
            res.add(bean);
        }
        addDataList(res, false);
    }

    // ---------- private ----------

    private static String getFilePath(String id) {
        int fileNum = id.hashCode() & (128 - 1);
        return String.format(INIT_PATH, fileNum);
    }
}
