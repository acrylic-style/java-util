package util.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public final class MemoryMeasure {
    private MemoryMeasure() {}

    public static long getSize(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.close();
            return baos.toByteArray().length;
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}
