package util.serialization;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.yaml.YamlObject;

public interface CustomClassSerializer<T> extends CustomClassSerializers.Serializer<T>, CustomClassSerializers.Deserializer<T> {
    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    static <T> CustomClassSerializer<T> of(@NotNull CustomClassSerializers.Serializer<T> serializer, @NotNull CustomClassSerializers.Deserializer<T> deserializer) {
        return new CustomClassSerializer<T>() {
            @Override
            public T deserialize(@NotNull YamlObject object) {
                return deserializer.deserialize(object);
            }

            @Override
            public void serialize(@NotNull YamlObject object, T instance) {
                serializer.serialize(object, instance);
            }
        };
    }
}
