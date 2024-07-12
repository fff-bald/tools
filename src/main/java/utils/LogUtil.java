package utils;

import java.util.Date;

/**
 * 日志工具类
 *
 * @author cjl
 * @since 2024/7/12 19:56
 */
public class LogUtil {

    private static final String ERROR = "_error";
    private static final String INFO = "_info";
    private static final String INIT_PATH = ".\\logs\\%s\\%s.txt";

    public static void info(String name, String content, Object... args) {
        log(getFilePath(name, INFO), content, args);
    }

    public static void error(String name, String content, Object... args) {
        log(getFilePath(name, ERROR), content, args);
    }

    private static void log(String path, String content, Object... args) {
        String log = String.format(content, args);
        FileUtil.writeStringToFile(path, log, true);
    }

    private static String getFilePath(String name, String type) {
        String date = TimeUtil.YYYY_MM_DD_SDF.format(new Date());
        String fileName = StringUtil.isBlank(name) ? type : name + type;
        return String.format(INIT_PATH, date, fileName);
    }
}