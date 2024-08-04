package funddata.utils;

import utils.NewUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static funddata.constant.FundDataConstant.ALL_FUND_IDS_URL;

/**
 * 工具类
 *
 * @author cjl
 * @since 2024/7/4 21:18
 */
public class FundUtil {


    // ---------- Wed data ----------

    /**
     * 获取全量基金ID
     *
     * @return
     */
    public static Set<String> getAllFundIdsFromWeb() {
        List<String> stringFromText = null;
        try {
            URL url = new URL(ALL_FUND_IDS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 可能需要设置一些请求头，例如 User-Agent，以模拟浏览器请求
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            // 读取响应
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // js文件被压缩过，只有一行需要处理
                    stringFromText = getStringFromText(line);
                }
                reader.close();
                inputStream.close();
            } else {
                System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> res = NewUtil.treeSet();
        int count = 0;
        for (int i = 0; i < stringFromText.size(); i += 5) {
            res.add(stringFromText.get(i));
            count++;
        }
        System.out.println("基金总数：" + count);
        return res;
    }

    // ---------- String handler ----------

    /**
     * 从一个字符串中分离出所有被双引号（"）包围的内容
     *
     * @param line
     * @return
     */
    public static List<String> getStringFromText(String line) {
        // 定义正则表达式，匹配双引号内的内容
        // 注意：这里使用非贪婪模式(.*?)来确保只匹配到最近的双引号
        Pattern pattern = Pattern.compile("\"(.*?)\"");

        Matcher matcher = pattern.matcher(line);
        List<String> matches = new ArrayList<>();

        // 查找所有匹配项
        while (matcher.find()) {
            // 使用group(1)获取括号中的匹配内容（即双引号内的内容）
            matches.add(matcher.group(1));
        }

        return matches;
    }

    /**
     * 从字符串中读取"pages："后面的数字
     *
     * @param input
     * @return
     */
    public static int getPagesValue(String input) {
        // 使用正则表达式匹配"pages:"后面跟着的数字
        String regex = "pages:(\\d+)";

        // Pattern用于编译正则表达式，Matcher用于执行匹配操作
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        // 查找匹配项
        if (matcher.find()) {
            // group(1)返回第一个捕获组（即数字部分）
            return Integer.parseInt(matcher.group(1));
        }

        // 如果没有找到匹配项，返回一个默认值或抛出异常
        throw new IllegalArgumentException("No 'pages' value found in the input string.");
    }

    // ---------- cal ----------

    /**
     * 计算复利年化收益率（按照一年365天来算）
     *
     * @return
     */
    public static double calYearChange(long day, double startPrice, double endPrice) {
        double year = day * 1.0d / 365;
        return Math.pow(endPrice / startPrice, 1.0d / year) - 1;
    }
}
