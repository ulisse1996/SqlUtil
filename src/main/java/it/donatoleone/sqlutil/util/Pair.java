package it.donatoleone.sqlutil.util;

import java.util.Map;
import java.util.Objects;

public class Pair<K,V> implements Map.Entry<K, V> {

    private final K key;
    private V value;
    private final boolean immutable;

    private Pair(K key, V value, boolean immutable) {
        this.key = Objects.requireNonNull(key);
        this.value = value;
        this.immutable = immutable;
    }

    public static <K,V> Pair<K,V> immutable(K key, V value) {
        return new Pair<>(key, value, true);
    }

    public static <K,V> Pair<K, V> mutable(K key, V value) {
        return new Pair<>(key, value, false);
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        if (immutable) {
            throw new IllegalArgumentException(MessageFactory.immutable());
        }
        this.value = value;
        return this.value;
    }
}
