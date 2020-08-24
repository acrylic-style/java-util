package util.memory;

import com.google.common.annotations.Beta;
import org.jetbrains.annotations.NotNull;
import util.jni.NativeCode;

import java.util.ArrayList;

@Beta
public class MemoryMeasure {
    private static final NativeCode NATIVE_CODE = new NativeCode("native-memory-measure");

    static {
        if (!NATIVE_CODE.load()) {
            System.err.println("Could not load native-memory-measure.so");
        }
    }

    private static final MemoryMeasure INSTANCE = new MemoryMeasure();

    @NotNull
    public static MemoryMeasure getInstance() { return INSTANCE; }

    @Beta
    public native long sizeof(Object o);
}
