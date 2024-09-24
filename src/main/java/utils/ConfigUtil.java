package utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final String CONFIG_INIT_FILE_PATH = ".\\resources\\config.properties";

    static {
        FileUtil.checkAndCreatePath(CONFIG_INIT_FILE_PATH);
    }

    private static final class InitPropertiesHolder {
        private static final Properties initProperties = getProperties(CONFIG_INIT_FILE_PATH);
    }

    public static Properties getProperties() {
        return InitPropertiesHolder.initProperties;
    }

    public static Properties getProperties(String filePath) {
        Properties properties = null;
        try {
            properties = new Properties();
            try (InputStream input = new FileInputStream(filePath)) {
                properties.load(input);
            }
        } catch (Exception e) {
            LogUtil.error("【ConfigUtil】获取配置文件错误：{}", ExceptionUtil.getStackTraceAsString(e));
        }
        return properties;
    }
}
