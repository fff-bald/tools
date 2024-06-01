package utils;

import java.io.*;
import java.util.List;

/**
 * @description: 操作文件工具类
 * @author: cjl
 * @date: 2024-06-01 14:43
 **/
public class FileUtil {

    /**
     * 逐行读取文件
     *
     * @param path 文件路径
     * @return
     */
    public static List<String> readFileByLine(String path) {
        List<String> res = NewUtil.arrayList();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                res.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 逐行写入文件
     *
     * @param path 文件路径
     * @param lines
     */
    public static void writeFileByLine(String path, List<String> lines) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 添加新行，如果你需要
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
