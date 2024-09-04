package utils;

import java.util.Objects;

/**
 * 数组工具类
 *
 * @author cjl
 * @since 2024/9/4 21:09
 */
public class ArrayUtil {
    /**
     * 查找数组中指定值的索引。
     *
     * @param <T>   数组元素的类型。
     * @param array 要搜索的数组，不能为null。
     * @param value 要在数组中查找的值，同样不能为null。
     * @return 如果找到值，则返回其在数组中的索引；如果未找到，则返回-1。
     */
    public static <T> int find(T[] array, T value) {
        if (array == null || value == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(value, array[i])) {
                return i;
            }
        }
        return -1;
    }
}