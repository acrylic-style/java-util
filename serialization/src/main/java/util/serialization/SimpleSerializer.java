package util.serialization;

import org.jetbrains.annotations.NotNull;
import util.SneakyThrow;

import java.io.*;
import java.util.Base64;

public class SimpleSerializer implements Serializable {
    public static final long serialVersionUID = 2L;

    private final Object object;

    public SimpleSerializer(Object o) {
        this.object = o;
    }

    @NotNull
    public static SimpleSerializer fromString(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return new SimpleSerializer(o);
        } catch (IOException | ClassNotFoundException e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }

    @NotNull
    public String asString() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this.object);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }

    public Object getObject() { return object; }

    @SuppressWarnings("unchecked")
    public <T> T get() { return (T) object; }
}
