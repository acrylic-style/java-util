package util.yaml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import util.base.Lists;
import util.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class YamlArray extends ArrayList<Object> implements YamlMember {
    private final Yaml yaml;

    public YamlArray(@NotNull Yaml yaml, @Nullable List<?> list) {
        super(MoreObjects.or(list, new ArrayList<>()));
        this.yaml = yaml;
    }

    public YamlArray(@Nullable List<?> list) {
        this(YamlConfiguration.DEFAULT, list);
    }

    public YamlArray(@NotNull Yaml yaml) { this(yaml, new ArrayList<>()); }

    public YamlArray() { this(YamlConfiguration.DEFAULT, new ArrayList<>()); }

    @NotNull
    @SuppressWarnings("unchecked")
    public YamlObject getObject(int index) { return new YamlObject(yaml, (Map<String, Object>) get(index)); }

    @NotNull
    @SuppressWarnings("unchecked")
    public YamlArray getArray(int index) { return new YamlArray(yaml, (List<Object>) get(index)); }

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
    public <T> void forEachAsType(Consumer<T> action) { forEach(o -> action.accept((T) o)); }


    @SuppressWarnings("unchecked")
    public @NotNull <F, T> YamlArray mapAsType(@NotNull BiFunction<F, Integer, T> function) {
        YamlArray newList = new YamlArray();
        Lists.forEachIndexed(this, (v, i) -> newList.add(function.apply((F) v, i)));
        return newList;
    }

    @SuppressWarnings("unchecked")
    public @NotNull <F, T> YamlArray mapAsType(@NotNull Function<F, T> function) {
        YamlArray newList = new YamlArray();
        this.forEach(v -> newList.add(function.apply((F) v)));
        return newList;
    }

    @Override
    public @NotNull Yaml getYaml() { return yaml; }

    @Override
    public @NotNull Object getRawData() { return this; }
}
