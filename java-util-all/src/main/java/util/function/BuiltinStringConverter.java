package util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class BuiltinStringConverter<T> implements StringConverter<T> {
    private static final Map<Class<?>, StringConverter<?>> converters = new HashMap<>();

    public static final BuiltinStringConverter<String> STRING = new BuiltinStringConverter<>(String.class, s -> s);
    public static final BuiltinStringConverter<Boolean> BOOLEAN = new BuiltinStringConverter<>(Boolean.class, Boolean::parseBoolean);
    public static final BuiltinStringConverter<Integer> INTEGER = new BuiltinStringConverter<>(Integer.class, Integer::parseInt);
    public static final BuiltinStringConverter<File> FILE = new BuiltinStringConverter<>(File.class, File::new);
    public static final BuiltinStringConverter<Byte> BYTE = new BuiltinStringConverter<>(Byte.class, Byte::parseByte);
    public static final BuiltinStringConverter<Short> SHORT = new BuiltinStringConverter<>(Short.class, Short::parseShort);
    public static final BuiltinStringConverter<Float> FLOAT = new BuiltinStringConverter<>(Float.class, Float::parseFloat);
    public static final BuiltinStringConverter<Long> LONG = new BuiltinStringConverter<>(Long.class, Long::parseLong);
    public static final BuiltinStringConverter<Double> DOUBLE = new BuiltinStringConverter<>(Double.class, Double::parseDouble);
    public static final BuiltinStringConverter<URI> URI = new BuiltinStringConverter<>(URI.class, s -> {
        try {
            return new URI(s);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    });
    public static final BuiltinStringConverter<URL> URL = new BuiltinStringConverter<>(URL.class, s -> {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    });

    private final StringConverter<T> converter;

    public BuiltinStringConverter(Class<T> clazz, StringConverter<T> converter) {
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
    public static <T> StringConverter<T> findConverter(Class<T> clazz) {
        if (!converters.containsKey(clazz)) return null;
        return (StringConverter<T>) converters.get(clazz);
    }

    @Override
    public T convert(@NotNull String s) { return converter.convert(s); }
}
