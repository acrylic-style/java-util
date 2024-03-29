package util.serialization;

import net.blueberrymc.native_util.NativeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ActionableResult;
import util.Chain;
import util.Validate;
import util.base.Lists;
import util.base.Maps;
import util.yaml.YamlArray;
import util.yaml.YamlConfiguration;
import util.yaml.YamlObject;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serializes/Deserializes an object that does not implement {@link Serializable}.
 * See <a href="https://github.com/acrylic-style/java-util/blob/master/java-util-all/src/test/java/test/util/ClassSerializerTest.java">here</a>
 * for examples.
 */
// TODO: super fields
public class ClassSerializer<T> {
    @NotNull
    private final Class<?> clazz;
    @NotNull
    private final T instance;
    @NotNull
    private final List<FieldInfo<?>> fields;

    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public ClassSerializer(@NotNull T instance) {
        this((Class<? extends T>) instance.getClass(), instance);
    }

    @Contract(pure = true)
    public ClassSerializer(@NotNull Class<? extends T> clazz, @NotNull T instance) {
        if (instance.equals(this)) {
            throw new IllegalArgumentException("Cannot serialize itself!");
        }
        this.clazz = clazz;
        this.instance = instance;
        this.fields = readFields(this.clazz, this.instance);
    }

    @NotNull
    public List<FieldInfo<?>> getFields() { return fields; }

    @NotNull
    public T getInstance() { return instance; }

    @NotNull
    public Class<?> getClazz() { return clazz; }

    @Contract(pure = true)
    @NotNull
    public YamlObject serializeToYaml() {
        YamlObject object = new YamlObject();
        serializeToYaml0(object);
        return object;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void serializeToYaml0(YamlObject object) {
        String className = this.clazz.getTypeName();
        if (instance instanceof Class) {
            object.set("type", "class");
            object.set("data", ((Class<?>) instance).getTypeName());
        } else if (instance instanceof String) {
            object.set("type", "string");
            object.set("data", instance);
        } else if (instance instanceof Integer || className.equals("java.lang.Integer")) {
            object.set("type", "integer");
            object.set("data", instance);
        } else if (instance instanceof Double || className.equals("java.lang.Double")) {
            object.set("type", "double");
            object.set("data", instance);
        } else if (instance instanceof Float || className.equals("java.lang.Float")) {
            object.set("type", "float");
            object.set("data", instance);
        } else if (instance instanceof Long || className.equals("java.lang.Long")) {
            object.set("type", "long");
            object.set("data", instance);
        } else if (instance instanceof Byte || className.equals("java.lang.Byte")) {
            object.set("type", "byte");
            object.set("data", instance);
        } else if (instance instanceof Short || className.equals("java.lang.Short")) {
            object.set("type", "short");
            object.set("data", instance);
        } else if (instance instanceof Boolean || className.equals("java.lang.Boolean")) {
            object.set("type", "boolean");
            object.set("data", instance);
        } else if (instance instanceof Enum) {
            object.set("type", "enum");
            object.set("class", this.clazz.getTypeName());
            object.set("data", ((Enum<?>) instance).name());
        } else if (instance instanceof WeakReference) {
            object.set("type", "weakref");
            Object v = ((WeakReference<?>) instance).get();
            object.setNullableObject("data", v == null ? null : new ClassSerializer<>(v).serializeToYaml());
        } else if (instance instanceof SoftReference) {
            object.set("type", "softref");
            Object v = ((SoftReference<?>) instance).get();
            object.setNullableObject("data", v == null ? null : new ClassSerializer<>(v).serializeToYaml());
        } else {
            if (instance instanceof Serializable) {
                object.set("type", "bytearray");
                object.set("data", new SimpleSerializer(instance).asString());
            } else {
                Map.Entry<Class<?>, CustomClassSerializer<?>> entry = Maps.findEntry(CustomClassSerializers.serializers, c -> c.isInstance(instance));
                if (entry != null) {
                    ((CustomClassSerializer) entry.getValue()).serialize(object, instance);
                    object.set("type", "custom");
                    object.set("class", entry.getKey().getTypeName());
                } else {
                    object.set("type", "object");
                    object.set("class", className);
                    object.set("fields", this.getFields().stream().map(FieldInfo::serializeToYaml).map(YamlObject::getRawData).collect(Collectors.toList()));
                }
            }
        }
    }

    @Contract(pure = true)
    @NotNull
    public String serialize() { return serializeToYaml().dump(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSerializer<?> that = (ClassSerializer<?>) o;
        if (!clazz.equals(that.clazz)) return false;
        if (!instance.equals(that.instance)) return false;
        return fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + instance.hashCode();
        result = 31 * result + fields.hashCode();
        return result;
    }

    // ----- static methods

    @Contract(pure = true)
    @NotNull
    public static List<FieldInfo<?>> readFields(@NotNull Class<?> clazz, @NotNull Object instance) {
        List<FieldInfo<?>> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue; // don't add static fields
            fields.add(FieldInfo.fromFieldWithObject(field, NativeUtil.get(field, instance)));
        }
        return fields;
    }

    @Contract(pure = true)
    public static <T> T deserialize(@Nullable Class<T> clazz, @NotNull String s) {
        Validate.notNull(s, "string cannot be null");
        return deserialize(clazz, new YamlConfiguration(YamlConfiguration.DEFAULT, s).asObject());
    }

    /**
     * Try to deserialize yaml object. Class may be null, and it will find class automatically if null. If not null,
     * the specified class will be used, but it you must specify the implementation class.
     * @param clazz the class (just for type)
     * @param object the yaml object data to deserialize
     * @param <T> output type
     * @throws RuntimeException if a target class could not be found
     * @return the deserialized value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Contract(value = "_, null -> null", pure = true)
    public static <T> T deserialize(@Nullable Class<T> clazz, @Nullable YamlObject object) {
        if (object == null) return null;
        String type = object.getString("type");
        Validate.notNull(type, "invalid type, got object: " + object.dump());
        // try using quick path
        try {
            switch (type) {
                case "class":
                    return (T) Class.forName(object.getString("data"));
                case "string":
                case "integer":
                case "double":
                case "float":
                case "long":
                case "byte":
                case "short":
                case "boolean":
                    return (T) object.getRawData().get("data"); // this is nullable
                case "enum":
                    return (T) Enum.valueOf((Class) Class.forName(object.getString("class")), object.getString("data"));
                case "bytearray":
                    return SimpleSerializer.fromString(object.getString("data")).get();
                case "weakref":
                    return object.getRawData().get("data") == null ? null : (T) new WeakReference<>(ClassSerializer.deserialize(null, object.getObject("data")));
                case "softref":
                    return object.getRawData().get("data") == null ? null : (T) new SoftReference<>(ClassSerializer.deserialize(null, object.getObject("data")));
                case "custom":
                    return (T) Objects.requireNonNull(Maps.find(CustomClassSerializers.serializers, Class.forName(object.getString("class")))).deserialize(object);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // and type should be object now, if not, throw exception
        if (!type.equals("object")) {
            throw new IllegalStateException("Invalid type '" + type + "'");
        }
        Class<?> cl = clazz != null && clazz.isInterface() ? null : clazz;
        if (cl == null) {
            try {
                cl = Class.forName(object.getString("class"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not deserialize class", e);
            }
        }
        Validate.notNull(cl, "class should not be null at this point");
        YamlArray fields = object.getArray("fields")
                .mapAsType((Function<Map<String, Object>, YamlObject>) YamlObject::new)
                .mapAsType((Function<YamlObject, FieldInfo>) FieldInfo::fromYaml);
        List<FieldInfo<?>> declaredFields = Arrays.stream(cl.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(FieldInfo::fromField)
                .collect(Collectors.toList());
        int expectedFCount = fields.size();
        int actualFCount = declaredFields.size();
        if (expectedFCount != actualFCount) {
            throw new IllegalArgumentException("Declared field count is not expected value, expected = " + expectedFCount + ", actual = " + actualFCount + ", class = " + cl.getTypeName());
        }
        AtomicInteger matches = new AtomicInteger();
        Lists.forEachIndexed(fields, (f, i) -> {
            if (f.equals(declaredFields.get(i))) matches.getAndIncrement();
        });
        if (matches.get() != expectedFCount) {
            throw new IllegalArgumentException("Fields are different than expected, expected = " + fields + ", actual = " + declaredFields + " (matches: " + matches.get() + ", expected: " + expectedFCount + "), class = " + cl.getTypeName());
        }
        Object[] args = fields.<FieldInfo, Object>mapAsType(FieldInfo::getInstance).toArray();
        Object inst;
        try {
            // try constructor first, this works for simple classes like SimpleEntry
            Map.Entry<? extends Constructor<?>, Object[]> entry = Objects.requireNonNull(tryFindConstructor(cl, args, (List) fields));
            inst = NativeUtil.newInstance(entry.getKey(), entry.getValue());
        } catch (NullPointerException e) {
            Object instance = NativeUtil.allocateInstance(cl);
            Class<?> finalCl = cl;
            fields.<FieldInfo<?>>forEachAsType(fieldInfo -> {
                try {
                    NativeUtil.set(finalCl.getDeclaredField(fieldInfo.getName()), instance, fieldInfo.getInstance());
                } catch (NoSuchFieldException noSuchFieldException) {
                    throw new UnsupportedOperationException(noSuchFieldException);
                }
            });
            inst = instance;
        }
        return (T) inst;
    }

    @Nullable
    public static <T> Map.Entry<Constructor<T>, Object[]> tryFindConstructor(Class<T> clazz, Object[] args, @NotNull List<FieldInfo<?>> fields) {
        Constructor<T> constructor = ActionableResult.getThrowable(() ->
                clazz.getDeclaredConstructor(fields.stream().map(FieldInfo::getClazz).toArray(Class[]::new))
        );
        if (constructor != null) return new AbstractMap.SimpleImmutableEntry<>(constructor, args);
        Class<?>[] classes1 = fields.stream().map(field -> {
            if (field.getActualType() != null) return field.getActualType();
            return field.getClazz();
        }).toArray(Class[]::new);
        Object[] args1 = args == null ? null : Lists.mapIndexed(Arrays.asList(args), (o, i) -> {
            if (o == null) return null;
            if (o instanceof Reference) {
                FieldInfo<?> field = fields.get(i);
                if (field != null && field.getActualType() != null) {
                    return ((Reference<?>) o).get();
                } else {
                    return o;
                }
            } else {
                return o;
            }
        }).toArray();
        if ((constructor = ActionableResult.getThrowable(() -> clazz.getDeclaredConstructor(classes1))) != null) {
            return new AbstractMap.SimpleImmutableEntry<>(constructor, args1);
        }
        return null;
    }
}
