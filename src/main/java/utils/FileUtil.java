package utils;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * @description: 操作文件工具类
 * @author: cjl
 * @date: 2024-06-01 14:43
 **/
public class FileUtil {

    /**
     * 逐行读取文件，文件不存在时返回空列表
     *
     * @param path 文件路径
     * @return
     */
    public static List<String> readFileByLine(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }

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
     * 将字符串写入文件
     *
     * @param path
     * @param str
     * @param isAppend 是否为追加写入
     */
    public static void writeStringToFile(String path, String str, boolean isAppend) {
        checkAndBuildDirectory(path);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path, isAppend));
            writer.write(str);
            writer.newLine(); // 添加新行，如果你需要
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

    /**
     * 逐行写入文件
     *
     * @param path
     * @param lines
     * @param isAppend 是否为追加写入
     */
    public static void writeStringLineToFile(String path, List<String> lines, boolean isAppend) {
        checkAndBuildDirectory(path);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path, isAppend));
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

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.delete()) {
            System.out.println(path + " 文件已被删除");
        } else {
            System.out.println(path + " 文件删除失败");
        }
    }

    // ---------- private ----------

    /**
     * 检查所有文件目录是否创建，存在没创建的就创建
     *
     * @param filePath
     */
    private static void checkAndBuildDirectory(String filePath) {
        // 从文件路径中分离出目录部分
        File file = new File(filePath);
        File directory = file.getParentFile();

        // 检查目录是否存在，如果不存在则创建它
        if (!directory.exists()) {
            boolean result = directory.mkdirs();

            if (result) {
                System.out.println("目录创建成功: " + directory.getAbsolutePath());
            } else {
                System.out.println("目录创建失败，请检查权限或磁盘空间");
                // 在这里可以添加额外的错误处理逻辑
            }
        }
    }
}
