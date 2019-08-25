package de.eldoria.shepard.util;

import java.util.HashMap;

public class DefaultMap<K, V> extends HashMap<K, V> {

    private final V defaultValue;

    /**
     * Creates a new default map with a default value if no key was found.
     *
     * @param defaultValue value which should be returned when no key was found.
     */
    public DefaultMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object key) {
        return super.getOrDefault(key, defaultValue);
    }

    /**
     * Returns a object or default value.
     *
     * @param key Key for lookup
     * @return Object or default value
     */
    public V getOrDefault(Object key) {
        return super.getOrDefault(key, defaultValue);
    }
}
