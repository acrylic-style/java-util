package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Boolean}
 */
public class InstStoreBoolean extends Instruction {
    private boolean flag;

    public InstStoreBoolean() {
        this(false);
    }

    public InstStoreBoolean(boolean flag) {
        super();
        this.flag = flag;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        flag = buf.get() == 1;
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.put((byte) (flag ? 1 : 0));
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return flag;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_BOOLEAN;
    }

    @Override
    public String toString() {
        return "InstStoreBoolean{" +
                "flag=" + flag +
                '}';
    }
}
