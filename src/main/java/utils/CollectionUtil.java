package utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 容器工具类
 *
 * @author cjl
 * @since 2024/9/1 22:52
 */
public class CollectionUtil {
    /**
     * 如果给定的键在映射中不存在，则使用给定的函数计算其值，并将其和键一起放入映射中，然后返回计算得到的新值。
     * 如果键已经存在于映射中，则直接返回与键关联的值。
     *
     * @param <K>           映射的键的类型
     * @param <V>           映射的值的类型
     * @param map           要操作的映射
     * @param key           要检查的键
     * @param valueSupplier 当键不存在时，用于计算新值的函数
     * @return 如果键不存在于映射中，则返回计算得到的新值；如果键已存在，则返回与键关联的旧值
     */
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

    /**
     * 查找列表中指定值的索引。
     *
     * @param <T>   列表元素的类型。
     * @param list  要搜索的列表，不能为null。
     * @param value 要在列表中查找的值，同样不能为null。
     * @return 如果找到值，则返回其在列表中的索引；如果未找到，则返回-1。
     */
    public static <T> int find(List<T> list, T value) {
        if (list == null || value == null) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(value, list.get(i))) {
                return i;
            }
        }
        return -1;
    }
}