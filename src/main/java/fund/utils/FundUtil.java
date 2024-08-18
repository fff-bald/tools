package fund.utils;

import fund.bean.FundBean;
import fund.handler.FundBeanHandlerEnum;
import utils.ExceptionUtil;
import utils.LogUtil;
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

import static fund.constant.FundConstant.ALL_FUND_IDS_URL;

/**
 * 工具类
 *
 * @author cjl
 * @since 2024/7/4 21:18
 */
public class FundUtil {

    // ---------- 流程相关 ----------

    /**
     * 检测数据处理是否全部完成
     *
     * @param bean
     * @return
     */
    public static boolean checkFinish(FundBean bean) {
        return bean.getState() == FundBeanHandlerEnum.FINISH.getId();
    }


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
            LogUtil.error("!!!异常信息：{}", ExceptionUtil.getStackTraceAsString(e));
        }

        Set<String> res = NewUtil.treeSet();
        int count = 0;
        for (int i = 0; i < stringFromText.size(); i += 5) {
            res.add(stringFromText.get(i));
            count++;
        }
        LogUtil.info("基金总数：{}", count);
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
     * 从输入字符串中提取指定标记后的数字值。
     * <p>
     * 该方法使用正则表达式从输入字符串中查找指定标记（如"pages:"）后面跟着的数字值。
     * 如果找到匹配项，则返回该数字值；如果没有找到匹配项，则抛出IllegalArgumentException异常。
     *
     * @param input 要检查的输入字符串。
     * @param mark  要查找的标记字符串（如"pages:"）。
     * @return 标记后面的数字值。
     * @throws IllegalArgumentException 如果在输入字符串中没有找到标记后的数字值，则抛出此异常。
     */
    public static int getPagesValue(String input, String mark) {
        // 使用正则表达式匹配"pages:"后面跟着的数字
        String regex = mark + "(\\d+)";

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
}
