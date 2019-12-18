package de.eldoria.shepard.webapi.apiobjects;

import java.time.LocalDateTime;

public class ApiCache<T> {
    private T object;
    private LocalDateTime cacheTime;
    private int invalidAfter;

    /**
     * Creates a new api cache object.
     *
     * @param object       object to cache
     * @param invalidAfter time in minutes how long the object is valid
     */
    public ApiCache(T object, int invalidAfter) {
        this.object = object;
        this.invalidAfter = invalidAfter;
        cacheTime = LocalDateTime.now();
    }

    /**
     * Checks if the cache is invalid.
     *
     * @return true if the cached object ist valid
     */
    public boolean isValid() {
        return cacheTime.plusMinutes(invalidAfter).isAfter(LocalDateTime.now());
    }

    /**
     * Get the object.
     *
     * @return the cached object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Update the object and refresh the cache.
     *
     * @param object new object.
     */
    public void update(T object) {
        cacheTime = LocalDateTime.now();
        this.object = object;
    }
}
