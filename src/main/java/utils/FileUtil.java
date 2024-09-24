package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        List<String> res = CollectionUtil.arrayList();
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
        checkAndCreatePath(path);

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
        checkAndCreatePath(path);

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
     * 创建路径上的所有文件夹，如果路径是文件，则在创建所有文件夹的同时创建文件
     *
     * @param pathStr 路径字符串
     * @throws IOException 如果创建文件或文件夹失败
     */
    public static void checkAndCreatePath(String pathStr) {
        Path path = Paths.get(pathStr);

        try {
            if (Files.isDirectory(path) || pathStr.endsWith(File.separator)) {
                // 如果路径是文件夹或以文件夹分隔符结尾，则创建所有文件夹
                Files.createDirectories(path);
                LogUtil.info("Directories created: {}", path);
            } else {
                // 如果路径是文件，则创建所有文件夹并创建文件
                Files.createDirectories(path.getParent());
                if (Files.notExists(path)) {
                    Files.createFile(path);
                    LogUtil.info("File created: {}", path);
                } else {
                    LogUtil.info("File already exists: {}", path);
                }
            }
        } catch (IOException e) {
            LogUtil.error("Failed to create path: {}", ExceptionUtil.getStackTraceAsString(e));
        }
    }
}

