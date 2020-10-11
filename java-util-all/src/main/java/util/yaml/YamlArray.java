package util.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class YamlArray extends CollectionList<Object> {
    public YamlArray(@Nullable List<Object> list) { super(list); }

    public YamlArray() { super(); }

    @NotNull
    @SuppressWarnings("unchecked")
    public YamlObject getObject(int index) { return new YamlObject((Map<String, Object>) get(index)); }

    @NotNull
    @SuppressWarnings("unchecked")
    public YamlArray getArray(int index) { return new YamlArray((List<Object>) get(index)); }

    public String getString(int index) { return (String) get(index); }

    public boolean getBoolean(int index) { return (boolean) get(index); }

    public Number getNumber(int index) { return (Number) get(index); }

    public int getInt(int index) { return getNumber(index).intValue(); }

    public float getFloat(int index) { return getNumber(index).floatValue(); }

    public double getDouble(int index) { return getNumber(index).doubleValue(); }

    public long getLong(int index) { return getNumber(index).longValue(); }

    public byte getByte(int index) { return getNumber(index).byteValue(); }

    public short getShort(int index) { return getNumber(index).shortValue(); }

    @SuppressWarnings("unchecked")
    public <T> void forEachAsType(Consumer<T> action) {
        forEach(o -> action.accept((T) o));
    }
}
