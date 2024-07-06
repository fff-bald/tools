package funddata;

import funddata.bean.FundDataBean;
import utils.FileUtil;
import utils.TimeUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        int count = 0;

        Set<String> allIds = FundDataCollationUtil.getAllFundIdsFromWeb();
        List<String> res = new LinkedList<>();
        for (String id : allIds) {
            try {
                FundDataBean bean = factory.createBean(id);
                res.add(bean.toString());
            } catch (Exception e) {
                System.out.println("---------- 发生异常，ID：" + id + " ----------");
                e.printStackTrace();
            }
            count++;
            if (count == 15) {
                break;
            }
        }
        FileUtil.writeFileByLine(path, res, true);
    }
}
