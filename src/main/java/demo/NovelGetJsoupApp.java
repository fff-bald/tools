package demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtil;
import utils.JsoupUtil;

public class NovelGetJsoupApp {

    private static final String PARAGRAPH_SELECTOR = "div.txtnav p";
    private static final String FILE_PATH = "C:\\Mine\\Downloads\\无尽债务（完结）.txt";
    private static final String INITIAL_URL = "https://69shuba.cx/txt/51195/33417537"; // 小说链接

    /**
     * 从给定的网页内容中提取小说文本内容
     *
     * @param doc 网页内容
     * @return 提取的小说文本内容
     */
    public static String extractNovelText(Document doc) {
        // 提取标题
        String title = doc.title();

        // 选择包含小说文本的元素
        Elements paragraphs = doc.select(PARAGRAPH_SELECTOR);

        // 提取并拼接小说文本
        StringBuilder novelText = new StringBuilder();
        novelText.append(title).append("\n\n"); // 将标题放在首行
        for (Element paragraph : paragraphs) {
            novelText.append(paragraph.text()).append("\n");
        }

        return novelText.toString();
    }

    public static void main(String[] args) {
        try {
            String url = INITIAL_URL;
            for (; ; ) {
                Document document = JsoupUtil.getDocumentThrow(url);
                String text = extractNovelText(document);
                FileUtil.writeStringToFile(FILE_PATH, text + "\n\n---\n\n", true); // 添加章节分隔符
                System.out.println(text);
                System.out.println(document.title());
                url = JsoupUtil.findElementUrlByText(document, "下一章");
                Thread.sleep(1000); // 避免频繁请求，添加延迟
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}