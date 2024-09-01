package utils;

import java.util.Map;
import java.util.function.Function;

/**
 * @author cjl
 * @since 2024/9/1 22:52
 */
public class CollectionUtil {
    public static <K, V> V computeIfAbsentAndReturnNewValue(Map<K, V> map, K key, Function<K, V> valueSupplier) {
        // 首先检查键是否存在
        if (!map.containsKey(key)) {
            // 键不存在，构建新值并放入
            V newValue = valueSupplier.apply(key);
            map.put(key, newValue);
            return newValue; // 返回新值
        } else {
            // 键已存在，返回旧值
            return map.get(key);
        }
    }
}
