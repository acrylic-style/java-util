package util.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @deprecated Use {@link net.blueberrymc.native_util.NativeUtil#getObjectSize} instead
 */
@Deprecated
public final class MemoryMeasure {
    private MemoryMeasure() {}

    /**
     * Measures the size of the object. The underlying object and their fields must be Serializable.
     * @return the size of the object in bytes.
     */
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
