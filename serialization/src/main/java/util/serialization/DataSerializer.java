package util.serialization;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @deprecated will be removed in 0.17
 */
@Deprecated
public class DataSerializer implements Serializable {
    public static final long serialVersionUID = 1L;

    private HashMap<String, Object> data = new HashMap<>();

    public Object get(String key) {
        return data.get(key);
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public String serialize() {
        SimpleSerializer serializer = new SimpleSerializer(data);
        return serializer.asString();
    }

    @NotNull
    public static DataSerializer fromString(String s) {
        DataSerializer dataSerializer = new DataSerializer();
        dataSerializer.data = SimpleSerializer.fromString(s).get();
        return dataSerializer;
    }
}
