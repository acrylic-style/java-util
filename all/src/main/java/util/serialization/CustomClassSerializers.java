package util.serialization;

import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.Validate;
import util.yaml.YamlObject;

import java.util.Map;

public final class CustomClassSerializers {
    static final Collection<Class<?>, CustomClassSerializer<?>> serializers = new Collection<>();

    public static <T> void register(@NotNull Class<T> forClass, @NotNull CustomClassSerializer<T> serializer) {
        Validate.notNull(forClass, "class cannot be null");
        Validate.notNull(serializer, "serializer cannot be null");
        serializers.put(forClass, serializer);
    }

    public static void unregister(@NotNull Class<?> clazz) {
        Validate.notNull(clazz, "class cannot be null");
        Map.Entry<Class<?>, CustomClassSerializer<?>> entry = serializers.clone().findEntry(clazz::equals);
        if (entry != null) serializers.remove(entry.getKey());
    }

    @FunctionalInterface
    public interface Serializer<T> {
        /**
         * Serializes the instance. You cannot set key 'type' and 'class' for yaml object. (it will be overridden)
         * @param object yaml object to set your custom data
         * @param instance the instance
         */
        void serialize(@NotNull YamlObject object, T instance);
    }

    @FunctionalInterface
    public interface Deserializer<T> {
        /**
         * Deserializes instance.
         * @param object the yaml object
         * @return the deserialized instance
         */
        T deserialize(@NotNull YamlObject object);
    }
}
