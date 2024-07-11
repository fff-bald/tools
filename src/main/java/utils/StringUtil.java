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
}
