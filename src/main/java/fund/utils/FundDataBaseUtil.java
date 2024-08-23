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
    public static void addDataList(List<FundDayBean> dayBeans, boolean isAppend, boolean needPersistence) {
        Set<String> table = NewUtil.hashSet();
        for (FundDayBean dayBean : dayBeans) {

            String filePath = getFilePath(dayBean.getId());
            if (!isAppend && !table.contains(filePath)) {
                FileUtil.writeStringToFile(getFilePath(dayBean.getId()), "", false);
                table.add(filePath);
            }

            addData(dayBean, needPersistence);
        }
    }

    /**
     * @param dataDayBean
     */
    public static void addData(FundDayBean dataDayBean, boolean needPersistence) {
        if (needPersistence && checkExistInDataBase(dataDayBean)) {
            return;
        }

        String id = dataDayBean.getId();
        dataDayBean.setPersistence(true);
        try {
            FileUtil.writeStringToFile(getFilePath(id), JsonUtil.toJson(dataDayBean), true);
        } catch (Exception e) {
            LogUtil.error("【{}】异常信息：{}", dataDayBean.getId(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    public static List<FundDayBean> getData(String id) {
        return getDataBeforeDate(id, "9999-12-30");
    }

    public static List<FundDayBean> getDataBeforeDate(String id, String date) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = NewUtil.arrayList();
        for (String str : strings) {
            if (!StringUtil.isBlank(str) && str.contains(id)) {
                try {
                    FundDayBean dayBean = JsonUtil.toObject(str, FundDayBean.class);
                    if (dayBean.getDate().compareTo(date) > 0) {
                        continue;
                    }
                    res.add(dayBean);
                } catch (Exception e) {
                    LogUtil.error("【{}】异常信息：{}", id, ExceptionUtil.getStackTraceAsString(e));
                }
            }
        }
        return res;
    }

    public static List<FundDayBean> getAllFileData(String id) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = NewUtil.arrayList();
        for (String str : strings) {
            try {
                if (StringUtil.isBlank(str)) {
                    continue;
                }
                FundDayBean dayBean = JsonUtil.toObject(str, FundDayBean.class);
                res.add(dayBean);
            } catch (Exception e) {
                LogUtil.error("【{}】异常信息：{}", id, ExceptionUtil.getStackTraceAsString(e));
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
        List<FundDayBean> data = getAllFileData(id);
        List<String> res = NewUtil.arrayList();
        for (FundDayBean bean : data) {

            if (bean.getId().equals(id)) {
                continue;
            }

            String key = bean.getId() + bean.getDate();
            if (set.contains(key)) {
                continue;
            }

            set.add(key);
            try {
                res.add(JsonUtil.toJson(bean));
            } catch (Exception e) {
                LogUtil.error("【{}】异常信息：{}", bean.getId(), ExceptionUtil.getStackTraceAsString(e));
            }
        }
        FileUtil.writeFileByLine(getFilePath(id), res, false);
    }

    // ---------- private ----------

    private static String getFilePath(String id) {
        int fileNum = id.hashCode() & (128 - 1);
        return String.format(INIT_PATH, fileNum);
    }

    // ---------- main ----------

    public static void main(String[] args) {
//        clearFundDataInDataBasById("001418");
//        clearFundDataInDataBasById("014844");
//        clearFundDataInDataBasById("014843");
//        clearFundDataInDataBasById("159569");
//        clearFundDataInDataBasById("161036");
//        clearFundDataInDataBasById("161035");
//        clearFundDataInDataBasById("161033");
//        clearFundDataInDataBasById("515910");
//        clearFundDataInDataBasById("010098");
        getDataBeforeDate("001819", "2024-07-31");
    }
}