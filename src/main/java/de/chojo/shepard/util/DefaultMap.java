package de.chojo.shepard.util;

import java.util.HashMap;

public class DefaultMap<K, V> extends HashMap<K,V>{

    private final V defaultValue;

    public DefaultMap(V defaultValue){
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object key) {
        return super.getOrDefault(key, defaultValue);
    }

    public V getOrDefault(Object key) {
        return super.getOrDefault(key, defaultValue);
    }
}
