package util.concurrent.ref;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Stores value into the DataCache, and clears the reference automatically (passively) when the TTL expires.
 */
public class DataCache<V> implements AutoCloseable, Cloneable {
    private V value;
    private long ttl;

    /**
     * Copies the DataCache into the another DataCache.
     * @param dataCache the DataCache
     * @return the new DataCache cloned from the DataCache from parameter.
     */
    @NotNull
    public static <V> DataCache<V> copy(@NotNull DataCache<V> dataCache) {
        return dataCache.clone();
    }

    /**
     * Creates DataCache with empty initial value and an unlimited TTL.
     */
    public DataCache() {
        this(null, -1);
    }

    /**
     * Creates DataCache with a specified initial value and an unlimited TTL.
     * @param initialValue the initial value
     */
    public DataCache(@Nullable V initialValue) {
        this(initialValue, -1);
    }

    /**
     * Creates DataCache with TTL (Time to live).
     * @param ttl Time to live in timestamp, -1 disables it.
     */
    public DataCache(long ttl) {
        this(null, ttl);
    }

    /**
     * Creates DataCache with TTL and the initial value.
     * @param initialValue the initial value
     * @param ttl Time to live in timestamp, -1 disables it.
     */
    public DataCache(V initialValue, long ttl) {
        this.value = initialValue;
        this.ttl = ttl;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    /**
     * Get the TTL (Time to live) value.
     * @return the TTL in timestamp
     */
    public long getTTL() {
        return this.ttl;
    }

    /**
     * Set the value.
     * @param value the value that will be stored into the object
     */
    public void set(V value) {
        this.value = value;
    }

    /**
     * Get the value. This method performs TTL check and if the TTL was expired, the value will be set to null and this
     * method returns null.
     * @return the stored value
     */
    public V get() {
        if (ttl != -1 && System.currentTimeMillis() > ttl) this.value = null;
        return value;
    }

    @NotNull
    public Optional<V> getOptional() {
        return Optional.ofNullable(get());
    }

    /**
     * Clears the object reference. This DataCache object still can be used if the any value was set. Also this method
     * may be called by the AutoCloseable. (e.g. try statement)
     */
    @Override
    public void close() {
        clear();
    }

    /**
     * Clears the object reference. This DataCache object still can be used if the any value was set.
     */
    public void clear() {
        this.value = null;
    }

    @Override
    public String toString() {
        return "DataCache{value=" + get() + ", ttl=" + ttl + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataCache<?> dataCache = (DataCache<?>) o;
        if (ttl != dataCache.ttl) return false;
        return Objects.equals(this.get(), dataCache.get());
    }

    @Override
    public int hashCode() {
        int result = get() != null ? value.hashCode() : 0;
        result = 31 * result + (int) (ttl ^ (ttl >>> 32));
        return result;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public DataCache<V> clone() {
        return new DataCache<>(this.get(), this.ttl);
    }
}
