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

    public static<T> List<T> ofList(T... vs) {
        List<T> list = new ArrayList<>();
        for (T v : vs) {
            list.add(v);
        }
        return list;
    }

    public static<T> Set<T> ofSet(T... vs) {
        Set<T> set = new HashSet<>();
        for (T v : vs) {
            set.add(v);
        }
        return set;
    }


    public static<K, V> Map<K, V> ofMap(K k1, V v1) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }
}
