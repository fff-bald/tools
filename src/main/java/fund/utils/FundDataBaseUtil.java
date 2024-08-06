package fund.utils;

import fund.bean.FundDayBean;
import utils.FileUtil;
import utils.JsonUtil;
import utils.LogUtil;
import utils.NewUtil;

import java.util.List;

import static fund.constant.FundConstant.LOG_NAME;

/**
 * 数据库工具类
 *
 * @author cjl
 * @since 2024/8/4 13:50
 */
public class FundDataBaseUtil {

    private static final String INIT_PATH = ".\\fund_data\\%s.txt";

    public static void addData(FundDayBean dataDayBean, boolean isCheck) {
        if (isCheck && checkExist(dataDayBean)) {
            return;
        }

        String id = dataDayBean.getId();
        try {
            FileUtil.writeStringToFile(getFilePath(id), JsonUtil.toJson(dataDayBean), true);
        } catch (Exception e) {
            LogUtil.error(LOG_NAME, "【%s】每日信息序列化异常，%s", dataDayBean.getId(), dataDayBean.toString());
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
                    LogUtil.error(LOG_NAME, "【%s】每日信息反序列化异常，%s", id, str);
                }
            }
        }
        return res;
    }

    public static boolean checkExist(FundDayBean dataDayBean) {
        for (FundDayBean dayBean : getData(dataDayBean.getId())) {
            if (dayBean.getDate().equals(dataDayBean.getDate())) {
                return true;
            }
        }
        return false;
    }

    // ---------- private ----------

    private static String getFilePath(String id) {
        int fileNum = id.hashCode() % 100;
        return String.format(INIT_PATH, fileNum);
    }
}
