package process;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtil;

import java.io.IOException;

public class NovelGetJsoupApp {
    // 基础URL用于处理相对路径
    private static final String BASE_URL = "https://www.69yuedu.net";
    private static final String FILE_PATH = "C:\\Mine\\Downloads\\你只管攻略.txt";

    public static void main(String[] args) {
        // 从第一章开始抓取
        String startUrl = "/r/drsgfbulfm/akhlubfztqudfpiy.html";
        crawlChapter(startUrl);
    }

    private static void crawlChapter(String chapterUrl) {
        try {
            // 1. 发起HTTP请求获取页面
            Document doc = Jsoup.connect(BASE_URL + chapterUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // 2. 提取章节内容
            Elements content = doc.select("div.content p.cp");
            System.out.println("\n=== 章节内容 ===");
            content.forEach(p -> FileUtil.writeStringToFile(FILE_PATH, p.text(), true)); // 添加章节分隔符));
            FileUtil.writeStringToFile(FILE_PATH, "\n\n---\n\n", true);

            // 3. 获取下一章链接
            Element nextLink = doc.selectFirst("div.page1 a:last-child");
            if (nextLink != null && nextLink.text().contains("下一章")) {
                String nextChapter = nextLink.attr("href");
                System.out.println("\n下一章链接: " + nextChapter);

                // 适当延迟防止被封
                Thread.sleep(3000);

                // 4. 递归抓取下一章
                crawlChapter(nextChapter);
            } else {
                System.out.println("已到达最后一章");
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
