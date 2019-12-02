package util;

import java.io.IOException;
import java.util.HashMap;

public class DataSerializer {
    private HashMap<String, Object> data = new HashMap<>();
    private DataSerializer() {}

    public Object get(String key) {
        return data.get(key);
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public String serialize() throws IOException {
        Serializer serializer = new Serializer(data);
        return serializer.asString();
    }

    @SuppressWarnings("unchecked")
    public static DataSerializer fromString(String s) throws IOException, ClassNotFoundException {
        DataSerializer dataSerializer = new DataSerializer();
        dataSerializer.data = (HashMap<String, Object>) Serializer.fromString(s).getObject();
        return dataSerializer;
    }

    public static DataSerializer newInstance() { return new DataSerializer(); }
}
