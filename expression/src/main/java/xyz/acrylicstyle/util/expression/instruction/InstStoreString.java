package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link String}
 */
public class InstStoreString extends Instruction {
    private String string;

    public InstStoreString() {
        this(null);
    }

    public InstStoreString(@Nullable String string) {
        super();
        this.string = string;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        string = getString(buf);
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        putString(buf, string);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        if (string == null) {
            throw new AssertionError("string was not loaded");
        }
        return string;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_STRING;
    }

    @Override
    public String toString() {
        return "InstStoreString{" +
                "string='" + string + '\'' +
                '}';
    }
}
