package demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @description: Jsoup Demo
 * @author: cjl
 * @date: 2024-06-01 21:46
 **/
public class JsoupApp {
    public static void main(String[] args) {
        testWindmillAnimation();
    }

    private static void testWindmillAnimation() {
        try {
            Document doc = Jsoup.connect("https://www.dm302.com/").get();
            System.out.println(doc.html());

            System.out.println("--------------------------------------------------------");
            System.out.println("------------------------- 隔绝 -------------------------");
            System.out.println("--------------------------------------------------------");

            // 解析HTML字符串
            // 使用CSS选择器定位到a标签
            Elements aTags = doc.select("a.item-link");

            // 遍历并打印每个<a>标签的title属性（如果存在）
            for (Element tag : aTags) {
                // 获取a标签下的所有span标签
                Elements spans = tag.select("span");

                // 遍历span标签并打印它们的文本内容
                for (Element span : spans) {
                    System.out.print(span.text() + "\t");
                }

                // 换行
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
