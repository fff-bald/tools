package utils;

/**
 * 常用字符串处理方法
 *
 * @author cjl
 * @since 2024/7/11 21:21
 */
public class StringUtil {
    /**
     * 判断字符串是否为null或长度为0
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 返回第一个字符串中被第二个和第三个字符串包围的子字符串。
     *
     * @param input     要处理的字符串
     * @param startStr  包围子字符串的起始字符串
     * @param endStr    包围子字符串的结束字符串
     * @return 被包围的子字符串，如果未找到则返回null
     */
    public static String getSubstringBetween(String input, String startStr, String endStr) {
        if (input == null || startStr == null || endStr == null) {
            throw new IllegalArgumentException("输入字符串和包围字符串不能为空");
        }

        int startIndex = input.indexOf(startStr);
        if (startIndex == -1) {
            return null; // 起始字符串未找到
        }

        startIndex += startStr.length(); // 移动到起始字符串之后的位置

        int endIndex = input.indexOf(endStr, startIndex);
        if (endIndex == -1) {
            return null; // 结束字符串未找到
        }

        return input.substring(startIndex, endIndex);
    }
}
