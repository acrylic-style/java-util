package util;

import java.io.*;
import java.util.Base64;

public class Serializer {
    private Object object;

    public Serializer(Object o) {
        this.object = o;
    }

    public static Serializer fromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return new Serializer(o);
    }

    public String asString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.object);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public Object getObject() { return object; }

    @SuppressWarnings("unchecked")
    public <T> T get() { return (T) object; }
}
