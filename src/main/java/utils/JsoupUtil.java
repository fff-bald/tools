package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Jsoup封装工具类
 *
 * @author cjl
 * @since 2024/7/13 11:18
 */
public class JsoupUtil {
    /**
     * 返回html文本内容包含特定字符串的Element
     *
     * @param tag
     * @param tagText
     * @return 存在多个返回仅第一个，找不到就抛异常
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

    /**
     * 查找第一个文本为指定字符串的元素，并返回该元素指向的网址。
     *
     * @param document   传入的Jsoup Document对象
     * @param searchText 要查找的文本
     * @return 该元素指向的网址，如果未找到则抛出异常
     */
    public static String findElementUrlByText(Document document, String searchText) {
        // 查找包含指定文本的所有元素
        Elements elements = document.getElementsContainingOwnText(searchText);

        // 遍历找到的元素，查找第一个文本完全匹配的元素
        for (Element element : elements) {
            if (element.ownText().equals(searchText)) {
                // 获取该元素的链接
                String url = element.attr("href");
                if (!url.isEmpty()) {
                    return url;
                }
            }
        }

        // 如果未找到匹配的元素或该元素没有链接，抛出异常
        throw new IllegalArgumentException("未找到匹配的元素或该元素没有链接");
    }

    /**
     * 该方法尝试通过指定的URL获取一个Document对象，并具有重试机制。
     * 如果连接由于IOException失败，它将重试最多100次，每次重试之间有10秒的延迟。
     * 如果所有尝试都失败，它将抛出IllegalArgumentException。
     *
     * @param url
     * @return
     * @throws InterruptedException
     */
    public static Document getDocumentThrow(String url) throws InterruptedException {
        return getDocumentThrow(url, 100, 10000);
    }


    public static Document getDocumentThrow(String url, int retryTime, int delayTIme) throws InterruptedException {
        Document document = null;
        for (int i = 0; i < retryTime; i++) {
            try {
                document = Jsoup.connect(url).get();
                break;
            } catch (IOException ioE) {
                Thread.sleep(delayTIme); // 延迟一段时间后重试
            }
        }

        if (document == null) {
            throw new IllegalArgumentException("Failed to obtain information through the URL.");
        }

        return document;
    }
}
