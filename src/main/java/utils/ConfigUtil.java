package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final String CONFIG_INIT_FILE_PATH = "config.properties";
    private static Properties properties = null;

    /**
     * 读取传入路径的文件，解析为 Properties
     *
     * @param path
     * @return
     */
    public static Properties loadConfig(String path) {
        Properties properties = new Properties();

        // 使用类加载器获取资源文件的输入流
        try (InputStream inputStream = ConfigUtil.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_INIT_FILE_PATH)) {

            if (inputStream == null) {
                LogUtil.error("无法找到config.properties文件");
            }

            // 加载properties文件
            properties.load(inputStream);
        } catch (IOException e) {
            LogUtil.error("无法找到config.properties文件");
        }

        return properties;
    }

    /**
     * 返回默认配置信息
     *
     * @return Properties对象，包含配置信息
     */
    public static Properties loadInitConfig() {

        if (ConfigUtil.properties == null) {
            Properties newProperties = loadConfig(CONFIG_INIT_FILE_PATH);
            ConfigUtil.properties = newProperties;
        }
        return ConfigUtil.properties;
    }

    /**
     * 根据key，在默认的Properties获取对应配置值
     *
     * @param key
     * @return 不存在会返回""
     */
    public static String getInitConfig(String key) {
        if (StringUtil.isBlank(key)) {
            return "";
        }

        Properties properties = loadInitConfig();
        return properties.getProperty(key, "");
    }

    // ---------- test ----------
    public static void main(String[] args) throws IOException {
        Properties properties = loadInitConfig();
        System.out.println(properties);
    }
}
