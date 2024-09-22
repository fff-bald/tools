package process.fund.utils;

import process.fund.bean.FundDayBean;
import utils.*;

import java.util.List;
import java.util.Map;
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
     * @param id
     * @param isAll      是否为该文件里的所有数据
     * @param beforeDate
     * @return
     */
    public static List<FundDayBean> getData(String id, boolean isAll, String beforeDate) {
        String filePath = getFilePath(id);
        List<String> strings = FileUtil.readFileByLine(filePath);
        List<FundDayBean> res = CollectionUtil.arrayList();
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
     * 通过基金ids将数据库里相关信息删除
     *
     * @param ids 所有列表内的id需要来自同一个文件夹
     */
    public static void clearFundDataInDataBaseByIds(List<String> ids) {

        if (ids == null || ids.size() == 0) {
            return;
        }

        Set<String> set = CollectionUtil.hashSet();
        List<FundDayBean> data = getAllFileData(ids.get(0));
        List<String> res = CollectionUtil.arrayList();
        for (FundDayBean bean : data) {

            String beanId = bean.getId();
            if (CollectionUtil.find(ids, beanId) != -1) {
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
        FileUtil.writeStringLineToFile(getFilePath(ids.get(0)), res, false);
        LogUtil.info(">>>数据库清除任务完成，清除ID为{}，清除记录{}条", ids, data.size() - res.size());
    }

    /**
     * 整理基金ID，并将其数据库里相关信息删除
     *
     * @param ids
     */
    public static void clearFundDataInDataBase(List<String> ids) {
        Map<String, List<String>> preHandlerMap = CollectionUtil.hashMap();

        for (String id : ids) {
            String filePath = getFilePath(id);
            List<String> idList = CollectionUtil.computeIfAbsentAndReturnNewValue(preHandlerMap,
                    filePath, key -> CollectionUtil.arrayList());
            idList.add(id);
        }

        for (Map.Entry<String, List<String>> entry : preHandlerMap.entrySet()) {
            clearFundDataInDataBaseByIds(entry.getValue());
        }
    }

    // ---------- private ----------

    private static String getFilePath(String id) {
        int fileNum = id.hashCode() & (128 - 1);
        return String.format(INIT_PATH, fileNum);
    }

    // ---------- main ----------

    public static void main(String[] args) {
    }
}