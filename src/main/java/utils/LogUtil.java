package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 *
 * @author cjl
 * @since 2024/7/12 19:56
 */
public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void debug(String content, Object... args) {
        logger.debug(content, args);
    }

    public static void info(String content, Object... args) {
        logger.info(content, args);
    }

    public static void warn(String content, Object... args) {
        logger.warn(content, args);
    }

    public static void error(String content, Object... args) {
        logger.error(content, args);
    }

    // ---------- private ----------
}