package com.happysnaker.utils;

import java.util.*;

/**
 * 在 2.0 版本调用如 List、Map、Set 的 of 方法时，项目会爆红（Kotlin 的原因），看上去很不舒服，因此此版本将全部做一个替代
 * @author Happysnaker
 * @description
 * @date 2022/6/30
 * @email happysnaker@foxmail.com
 */
public class OfUtil {

    @SafeVarargs
    public static<T> List<T> ofList(T... vs) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, vs);
        return list;
    }

    @SafeVarargs
    public static<T> Set<T> ofSet(T... vs) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, vs);
        return set;
    }


    public static<K, V> Map<K, V> ofMap(K k1, V v1) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }


    public static<K, V> Map<K, V> ofMap(List<K> keys, List<V> vals) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), vals.get(i));
        }
        return map;
    }
}
