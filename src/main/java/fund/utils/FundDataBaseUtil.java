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
     * 常规途径：将数据追加写入本地数据库，会过滤掉本地已标记数据
     *
     * @param dayBeans
     */
    public static void addDataList(List<FundDayBean> dayBeans) {
        for (FundDayBean dayBean : dayBeans) {
            addData(dayBean);
        }
    }

    /**
     * 常规途径：将数据追加写入本地数据库，会过滤掉本地已标记数据
     *
     * @param dataDayBean
     */
    public static void addData(FundDayBean dataDayBean) {
        if (checkExistInDataBase(dataDayBean)) {
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

    public static List<FundDayBean> getDataBeforeDate(String id, String beforeDate) {
        return getData(id, false, beforeDate);
    }

    public static List<FundDayBean> getAllFileData(String id) {
        return getData(id, true, "9999-12-30");
    }

    /**
     *
     * @param id
     * @param isAll 是否为该文件里的所有数据
     * @param beforeDate
     * @return
     */
    public static List<FundDayBean> getData(String id, boolean isAll, String beforeDate) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = NewUtil.arrayList();
        for (String str : strings) {
            if (StringUtil.isBlank(str)) {
                continue;
            }
            if (!isAll && !str.contains(id)) {
                continue;
            }
            FundDayBean dayBean = null;
            try {
                dayBean = JsonUtil.toObject(str, FundDayBean.class);
            } catch (Exception e) {
                LogUtil.error("【{}】异常信息：{}", id, ExceptionUtil.getStackTraceAsString(e));
            }
            if (dayBean.getDate().compareTo(beforeDate) > 0) {
                continue;
            }
            res.add(dayBean);
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

            // 顺便对文件里的其他数据做个去重
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
        clearFundDataInDataBasById("161035");
        clearFundDataInDataBasById("161036");
        clearFundDataInDataBasById("161033");
    }
}