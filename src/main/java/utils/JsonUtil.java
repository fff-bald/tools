package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author cjl
 * @since 2024/8/4 13:47
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON字符串转对象
    public static <T> T toObject(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    // 对象转JSON字符串
    public static String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
