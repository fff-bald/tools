package demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description: 爬虫，功能有图片爬取
 * @author: cjl
 * @date: 2024-06-01 22:56
 **/
public class ImageCrawlerApp {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String IMAGE_SAVE_DIR = "downloaded_images";

    public static void main(String[] args) {
        String url = "https://www.dm302.com/"; // 替换为你要爬取的网页URL
        try {
            crawlImages(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void crawlImages(String url) throws IOException {
        // 设置请求头，模拟浏览器访问
        Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

        // 使用CSS选择器查找所有带有data-original属性的a标签（懒加载了，直接查查不到）
        Elements elements = doc.select("a");

        // 创建保存图片的目录
        Path saveDir = Paths.get(IMAGE_SAVE_DIR);
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir);
        }

        for (Element image : elements) {
            String imageUrl = image.attr("data-original");
            // 处理相对URL或不完整URL
            if (!imageUrl.startsWith("http")) {
                imageUrl = resolveUrl(url, imageUrl);
            }
            // 下载图片
            downloadImage(imageUrl, saveDir);
        }
    }

    private static String resolveUrl(String baseUrl, String relativeUrl) {
        // 这里简化处理，实际情况可能更复杂，需要考虑查询参数、锚点等
        if (relativeUrl.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.indexOf("/", baseUrl.indexOf("//") + 2)) + relativeUrl;
        } else {
            int lastIndex = baseUrl.lastIndexOf("/");
            return baseUrl.substring(0, lastIndex + 1) + relativeUrl;
        }
    }

    private static void downloadImage(String imageUrl, Path saveDir) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        InputStream in = new BufferedInputStream(connection.getInputStream());
        String fileName = url.getFile();
        // 简单的文件名处理，实际情况可能需要更复杂的处理逻辑来避免文件名冲突等问题
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        Path savePath = saveDir.resolve(fileName);
        try (FileOutputStream out = new FileOutputStream(savePath.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            in.close();
        }
        System.out.println("Downloaded image to " + savePath);
    }
}