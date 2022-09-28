package com.happysnaker.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapGetter implements Cloneable {
    private Map<Object, Object> map;

    @Override
    public String toString() {
        return "MapGetter{" +
                "map=" + map +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapGetter mapGetter = (MapGetter) o;
        return Objects.equals(map, mapGetter.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    public MapGetter(Object obj) {
        if (obj instanceof  Map) {
            this.map = (Map) obj;
        } else if (obj instanceof MapGetter) {
            this.map = ((MapGetter) obj).map;
        } else {
            throw new RuntimeException("不合法的构造参数");
        }
    }

    public MapGetter(Map map) {
        this.map = map;
    }

    public MapGetter(MapGetter mg) {
        this.map = mg.map;
    }


    @Override
    public MapGetter clone() throws CloneNotSupportedException {
        MapGetter clone = (MapGetter) super.clone();
        clone.map = new HashMap<>(this.map);
        return clone;
    }

    public MapGetter getMapGetter(Object key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return new MapGetter((Map) map.get(key));
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public<T> T get(Object key, Class<T> tClass) {
        return (T) map.get(key);
    }


    public<T> T getOrDefault(Object key, T defVal, Class<T> tClass) {
        return (T) map.getOrDefault(key, defVal);
    }

    public String getString(Object key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return (String) map.get(key);
    }

    public String getStringOrDefault(Object key, String val) {
        if (!map.containsKey(key)) {
            return val;
        }
        return (String) map.get(key);
    }

    public long getLong(Object key) {
        return Long.parseLong(map.get(key).toString());
    }

    public int getInt(Object key) {
        return Integer.parseInt(map.get(key).toString());
    }

    public double getDouble(Object key) {
        return Double.parseDouble(map.get(key).toString());
    }

    public float getFloat(Object key) {
        return Float.parseFloat(map.get(key).toString());
    }

    public List getList(Object key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return (List) map.get(key);
    }

    public<T> List<T> getList(Object key, Class<T> tClass) {
        if (!map.containsKey(key)) {
            return null;
        }
        return (List<T>) map.get(key);
    }

    public boolean getBoolean(Object key) {
        return Boolean.parseBoolean(map.get(key).toString());
    }
}
