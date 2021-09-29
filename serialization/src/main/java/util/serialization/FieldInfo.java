package util.serialization;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ActionableResult;
import util.yaml.YamlConfiguration;
import util.yaml.YamlObject;

import java.lang.reflect.Field;

public class FieldInfo<T> {
    @NotNull
    private final Class<? extends T> clazz;
    @NotNull
    private final String name;
    @Nullable
    private final T instance;

    private Class<?> actualType = null;

    @Contract(pure = true)
    public FieldInfo(@NotNull Class<? extends T> clazz, @NotNull String name, @Nullable T instance) {
        this.clazz = clazz;
        this.name = name;
        this.instance = instance;
    }

    @Contract(value = "_ -> this", mutates = "this")
    private FieldInfo<T> actualType(Class<?> actualType) {
        this.actualType = actualType;
        return this;
    }

    public Class<?> getActualType() { return actualType; }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static FieldInfo<?> fromString(@NotNull String s) {
        YamlObject object = new YamlConfiguration(YamlConfiguration.DEFAULT, s).asObject();
        return fromYaml(object);
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static FieldInfo<?> fromField(@NotNull Field field) { return fromFieldWithObject(field, null); }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static FieldInfo<?> fromFieldWithObject(@NotNull Field field, @Nullable Object instance) {
        ActualType actualType = field.getAnnotation(ActualType.class);
        return new FieldInfo<>(field.getType(), field.getName(), instance).actualType(actualType == null ? null : actualType.value());
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static FieldInfo<?> fromYaml(@NotNull YamlObject object) {
        String className = object.getString("class");
        if (className == null) throw new RuntimeException("Invalid FieldInfo structure: className is null");
        Class<?> clazz = ActionableResult
                .ofThrowable(() -> Class.forName(className))
                .throwIfAny()
                .orElseThrow(() -> new RuntimeException("Class.forName(className) returned null"));
        String name = object.getString("name");
        if (name == null) throw new RuntimeException("Invalid FieldInfo structure: name is null");
        YamlObject instance = object.getObject("instance");
        Class<?> actualType = ActionableResult
                .ofThrowable(() -> object.getString("actualType") == null ? null : Class.forName(object.getString("actualType")))
                .throwIfAny()
                .get();
        return new FieldInfo<>(clazz, name, ClassSerializer.deserialize(clazz, instance)).actualType(actualType);
    }

    @NotNull
    public String serialize() { return serializeToYaml().dump(); }

    @NotNull
    public YamlObject serializeToYaml() {
        YamlObject object = new YamlObject();
        object.set("class", clazz.getTypeName());
        object.set("name", name);
        object.set("instance", instance == null ? null : new ClassSerializer<>(instance).serializeToYaml().getRawData());
        object.setNullable("actualType", actualType == null ? null : actualType.getTypeName());
        return object;
    }

    @NotNull
    public Class<?> getClazz() { return clazz; }

    @NotNull
    public String getName() { return name; }

    @Nullable
    public T getInstance() { return instance; }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo<?> fieldInfo = (FieldInfo<?>) o;
        if (!clazz.equals(fieldInfo.clazz)) return false;
        return name.equals(fieldInfo.name);
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" + "clazz=" + clazz +
                ", name='" + name + '\'' +
                '}';
    }
}
