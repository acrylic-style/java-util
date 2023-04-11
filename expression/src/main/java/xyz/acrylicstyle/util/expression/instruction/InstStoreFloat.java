package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Float}
 */
public class InstStoreFloat extends Instruction {
    private float n;

    public InstStoreFloat() {
        this(0);
    }

    public InstStoreFloat(float n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.getFloat();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putFloat(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_FLOAT;
    }

    @Override
    public String toString() {
        return "InstStoreFloat{" +
                "n=" + n +
                '}';
    }
}
