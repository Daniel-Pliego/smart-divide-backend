package mr.limpios.smart_divide_backend.aplication.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <T, K, V> Map<K, V> toMap(
            Collection<T> source,
            Function<T, K> keyExtractor,
            Function<T, V> valueExtractor
    ) {
        if (source == null) {
            return Map.of();
        }
        return source.stream()
                .collect(Collectors.toMap(keyExtractor, valueExtractor));
    }
}