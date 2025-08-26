package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final String CONFIG_INIT_FILE_PATH = "config.properties";

    /**
     * 读取资源目录下的config.properties文件
     *
     * @return Properties对象，包含配置信息
     */
    public static Properties loadConfig() {
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

    // ---------- test ----------
    public static void main(String[] args) throws IOException {
        Properties properties = loadConfig();
        System.out.println(properties);
    }
}
