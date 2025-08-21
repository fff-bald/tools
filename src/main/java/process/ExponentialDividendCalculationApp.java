package process;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utils.*;

import java.util.List;
import java.util.Map;

import static utils.StringUtil.SPLIT_DAOHAO;

/**
 * 指数股息计算demo
 *
 * @author cjl
 * @since 2025/6/22 22:52
 */
public class ExponentialDividendCalculationApp {

    private static final String dataUrl = "";

    public static void main(String[] args) throws Exception {
        // 获取指数权重
        Map<String, Double> exponentialMap = getExponentialMap("");

        double res = 0;
        for (Map.Entry<String, Double> entry : exponentialMap.entrySet()) {
            double dividend = getDividend(entry.getKey());
            System.out.println("成份券代码" + entry.getKey() + " 权重" + entry.getValue() * 100 + " 股息率" + dividend + " 时间" + DateUtil.getCurrentTime());
            res += entry.getValue() * dividend;
            Thread.sleep(3000);
        }

        System.out.println("指数股息率为：" + res);
    }

    // ---------- utils ----------

    private static double getDividend(String id) {
        double res = 0;
        String format = String.format(dataUrl, id);
        try {
            Document documentThrow = JsoupUtil.getDocumentThrow(format, 10, 10000);
            Elements select = documentThrow.select("#infoBox股息率");
            String text = select.text();
            res = Double.parseDouble(text.substring(0, text.indexOf("%")));
        } catch (Exception e) {
            System.out.println("getDividend方法报错： 成份券代码" + id + " 报错内容" + ExceptionUtil.getStackTraceAsString(e));
        }
        return res;
    }

    private static Map<String, Double> getExponentialMap(String filePath) {
        Map<String, Double> res = CollectionUtil.hashMap();
        List<String> strings = FileUtil.readFileByLine(filePath);
        for (int i = 1; i < strings.size(); i++) {
            String line = strings.get(i);
            String[] split = line.split(SPLIT_DAOHAO);
            String key = strToUrlMark(line, split[4]);
            double value = Double.parseDouble(split[split.length - 1]) / 100;
            res.put(key, value);
        }
        return res;
    }

    private static String strToUrlMark(String line, String id) {
        if (line.contains("深圳证券交易所")) {
            return "sz" + id;
        }
        if (line.contains("上海证券交易所")) {
            return "sh" + id;
        }
        return "";
    }
}