package utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

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
     * 返回包含特定attr的第一个Element
     *
     * @param divElements
     * @param attrName
     * @return 存在多个返回仅第一个，找不到就抛异常
     */
    public static Element findElementWithAttr(Elements divElements, String attrName) {
        for (Element res : divElements) {
            if (res.hasAttr(attrName)) {
                return res;
            }
        }
        // 如果没有找到匹配项，返回一个默认值或抛出异常
        throw new IllegalArgumentException("No Exist Element With This Attr");
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

    /**
     * 模拟浏览器请求获取网页内容
     *
     * @param url 要抓取的网页 URL
     * @return 抓取到的网页内容的 Document 对象，可能为null
     */
    public static Document getDocumentSimulate(String url) {
        // 设置 User-Agent 和其他头部信息
        Map<String, String> headers = CollectionUtil.hashMap();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        headers.put("Referer", "https://www.google.com/");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        // 设置 Cookies
        Map<String, String> cookies = CollectionUtil.hashMap();
        cookies.put("cookie_name", "cookie_value");

        Document doc = null;
        try {
            // 创建 Jsoup 连接并设置头部信息和 Cookies
            Connection connection = Jsoup.connect(url)
                    .headers(headers)
                    .cookies(cookies)
                    .timeout(10000) // 设置超时时间为 10 秒
                    .method(Connection.Method.GET);

            // 发送 GET 请求并获取响应的 Document 对象
            doc = connection.get();
        } catch (IOException e) {
            // 捕获并打印异常
            e.printStackTrace();
        }

        // 返回抓取到的 Document 对象
        return doc;
    }

    // ---------- main ----------
    public static void main(String[] args) {
        Document documentSimulate = getDocumentSimulate("https://69shuba.cx/txt/76475/39108317");
        System.out.println(documentSimulate.text());
    }
}
