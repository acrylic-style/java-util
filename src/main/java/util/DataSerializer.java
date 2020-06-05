package util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

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
        Serializer serializer = new Serializer(data);
        return serializer.asString();
    }

    @NotNull
    public static DataSerializer fromString(String s) {
        DataSerializer dataSerializer = new DataSerializer();
        dataSerializer.data = Serializer.fromString(s).get();
        return dataSerializer;
    }
}
