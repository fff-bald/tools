package utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 解析html工具
 *
 * @author cjl
 * @since 2024/7/13 11:18
 */
public class HtmlUtil {
    /**
     * 返回文本内容包含特定字符串的Element，存在多个返回第一个
     *
     * @param tag
     * @param tagText
     * @return
     */
    public static Element findElement(Elements tag, String tagText) {
        for (Element res : tag) {
            if (res.text().contains(tagText)) {
                return res;
            }
        }
        // 如果没有找到匹配项，返回一个默认值或抛出异常
        throw new IllegalArgumentException("No Exist Tag With Str");
    }
}
