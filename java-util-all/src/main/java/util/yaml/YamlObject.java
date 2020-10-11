package util.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlObject {
    private final Map<String, Object> map;

    public YamlObject(@Nullable Map<String, Object> map) {
        this.map = map == null ? new HashMap<>() : map;
    }

    public YamlObject() {
        this.map = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public YamlObject getObject(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        if (!this.map.containsKey(key)) return null;
        Object o = this.map.get(key);
        if (o instanceof Map) {
            return new YamlObject((Map<String, Object>) o);
        } else {
            return new YamlObject();
        }
    }

    @SuppressWarnings("unchecked")
    public YamlArray getArray(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        if (!this.map.containsKey(key)) return null;
        Object o = this.map.get(key);
        if (o instanceof List) {
            return new YamlArray((List<Object>) o);
        } else {
            return new YamlArray();
        }
    }

    public String getString(@NotNull String key) {
        if (!this.map.containsKey(key) || this.map.get(key) == null) return null;
        return this.map.get(key).toString();
    }

    public String getString(@NotNull String key, @Nullable String def) {
        String s = getString(key);
        return s == null ? def : s;
    }

    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NotNull String key, boolean def) {
        if (!this.map.containsKey(key) || this.map.get(key) == null) return def;
        Object o = this.map.get(key);
        if (o instanceof Boolean) return (boolean) o;
        return def;
    }

    public Number getNumber(@NotNull String key) {
        if (!this.map.containsKey(key) || this.map.get(key) == null) return null;
        Object o = this.map.get(key);
        if (o instanceof Number) return (Number) o;
        return null;
    }

    public Number getNumber(@NotNull String key, Number def) {
        Number number = getNumber(key);
        return number == null ? def : number;
    }

    public int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    public int getInt(@NotNull String key, int def) {
        return getNumber(key, def).intValue();
    }

    public float getFloat(@NotNull String key) {
        return getFloat(key, 0);
    }

    public float getFloat(@NotNull String key, float def) {
        return getNumber(key, def).floatValue();
    }

    public long getLong(@NotNull String key) {
        return getLong(key, 0);
    }

    public long getLong(@NotNull String key, long def) {
        return getNumber(key, def).longValue();
    }

    public double getDouble(@NotNull String key) {
        return getDouble(key, 0);
    }

    public double getDouble(@NotNull String key, double def) {
        return getNumber(key, def).doubleValue();
    }

    public byte getByte(@NotNull String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(@NotNull String key, byte def) {
        return getNumber(key, def).byteValue();
    }

    public short getShort(@NotNull String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(@NotNull String key, short def) {
        return getNumber(key, def).shortValue();
    }
}
