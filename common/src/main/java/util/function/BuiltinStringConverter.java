package util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class BuiltinStringConverter<T> implements StringConverter<T> {
    private static final Map<Class<?>, StringConverter<?>> converters = new HashMap<>();

    public static final StringConverter<String> STRING = new BuiltinStringConverter<>(String.class, s -> s);
    public static final StringConverter<Boolean> BOOLEAN = new BuiltinStringConverter<>(Boolean.class, Boolean::parseBoolean);
    public static final StringConverter<Integer> INTEGER = new BuiltinStringConverter<>(Integer.class, Integer::parseInt);
    public static final StringConverter<File> FILE = new BuiltinStringConverter<>(File.class, File::new);
    public static final StringConverter<Byte> BYTE = new BuiltinStringConverter<>(Byte.class, Byte::parseByte);
    public static final StringConverter<Short> SHORT = new BuiltinStringConverter<>(Short.class, Short::parseShort);
    public static final StringConverter<Float> FLOAT = new BuiltinStringConverter<>(Float.class, Float::parseFloat);
    public static final StringConverter<Long> LONG = new BuiltinStringConverter<>(Long.class, Long::parseLong);
    public static final StringConverter<Double> DOUBLE = new BuiltinStringConverter<>(Double.class, Double::parseDouble);
    public static final StringConverter<URI> URI = new BuiltinStringConverter<>(URI.class, URI::new);
    public static final StringConverter<URL> URL = new BuiltinStringConverter<>(URL.class, URL::new);

    @NotNull private final StringConverter<T> converter;

    public BuiltinStringConverter(@NotNull Class<T> clazz, @NotNull ThrowableStringConverter<T> converter) {
        this.converter = converter;
        converters.put(clazz, converter);
        Class<?> clazz2 = null;
        if (clazz == Integer.class) clazz2 = int.class;
        if (clazz == Boolean.class) clazz2 = boolean.class;
        if (clazz == Byte.class) clazz2 = byte.class;
        if (clazz == Short.class) clazz2 = short.class;
        if (clazz == Float.class) clazz2 = float.class;
        if (clazz == Long.class) clazz2 = long.class;
        if (clazz == Double.class) clazz2 = double.class;
        if (clazz2 != null) converters.put(clazz2, converter);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> StringConverter<T> findConverter(@NotNull Class<T> clazz) {
        return (StringConverter<T>) converters.get(clazz); // uses #equals
    }

    @Override
    public T convert(@NotNull String s) { return converter.convert(s); }
}
