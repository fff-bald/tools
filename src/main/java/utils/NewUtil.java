package utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: cjl
 * @date: 2024-06-01 15:03
 * @description: new容器统一管理工具
 **/
public class NewUtil {
    public static <T> ArrayList<T> arrayList() {
        return new ArrayList<>(4);
    }

    public static <K, V> HashMap<K, V> hashMap() {
        return new HashMap<>(4);
    }
}
