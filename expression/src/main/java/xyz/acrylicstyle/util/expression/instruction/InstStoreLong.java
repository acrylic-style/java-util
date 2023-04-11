package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Long}
 */
public class InstStoreLong extends Instruction {
    private long n;

    public InstStoreLong() {
        this(0);
    }

    public InstStoreLong(long n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.getLong();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putLong(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_LONG;
    }

    @Override
    public String toString() {
        return "InstStoreLong{" +
                "n=" + n +
                '}';
    }
}
