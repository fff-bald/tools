package utils;

import java.util.*;

/**
 * @author: cjl
 * @date: 2024-06-01 15:03
 * @description: new容器统一管理工具
 **/
public class NewUtil {
    public static <T> List<T> arrayList() {
        return new ArrayList<>(4);
    }

    public static <T> List<T> arrayList(int size) {
        return new ArrayList<>(size);
    }

    public static <T> List<T> arraySycnList() {
        return Collections.synchronizedList(new ArrayList<>(4));
    }

    public static <K, V> Map<K, V> hashMap() {
        return new HashMap<>(4);
    }

    public static <K> Set<K> hashSet() {
        return new HashSet<>(4);
    }

    public static <K extends Comparable<K>> Set<K> treeSet() {
        return new TreeSet<>();
    }

    public static <K extends Comparable<K>, V> Map<K, V> treeMap() {
        return new TreeMap<>();
    }
}
