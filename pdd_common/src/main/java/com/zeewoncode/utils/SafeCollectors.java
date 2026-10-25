package com.zeewoncode.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SafeCollectors {

    private SafeCollectors() { throw new UnsupportedOperationException(); }

    //安全的 List 转 Map
    public static <E, K> Map<K, E> toMap(List<E> list, Function<E, K> keyFn) {
        if (list == null || list.isEmpty()) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyFn, Function.identity(), (a, b) -> a));
    }

    //安全的字段提取 + 去重
    public static <E, V> List<V> distinctValues(Collection<E> src, Function<E, V> fn) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        return src.stream().map(fn).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }
}
