package funddata;

import funddata.bean.FundDataBean;
import utils.FileUtil;
import utils.StringUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static funddata.constant.FundDataConstant.FILE_ABSOLUTE_PATH;

/**
 * 基金数据爬取整理APP
 *
 * @author cjl
 * @since 2024/7/4.
 */
public class FundDataCollationApp {
    /**
     * 入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        FundDataBeanFactory factory = FundDataBeanFactory.getInstance();
        String path = String.format(FILE_ABSOLUTE_PATH, "base-" + TimeUtil.YYYY_MM_DD_SDF.format(new Date()));

        Set<String> allIds = FundDataCollationUtil.getAllFundIdsFromWeb();
        List<String> res = new LinkedList<>();
        res.add(FundDataCollationUtil.getAllFieldsExceptList(FundDataBean.class));

        for (String id : allIds) {
            System.out.println(String.format("【%s】开始处理", id));
            try {
                FundDataBean bean = factory.createBean(id);
                res.add("'" + FundDataCollationUtil.getAllFieldValuesExceptList(bean));
            } catch (Exception e) {
                System.out.println(String.format("【%s】发生异常", id));
                e.printStackTrace();
            }
            System.out.println(String.format("【%s】处理完成", id));
            if (res.size() % 50 == 0) {
                FileUtil.writeFileByLine(path, res, true);
                res.clear();
            }
        }
    }
}
