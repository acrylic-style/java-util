package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Objects;

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
        return getString();
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_STRING;
    }

    public @NotNull String getString() {
        return Objects.requireNonNull(string, "string was not loaded");
    }

    @Override
    public String toString() {
        return "InstStoreString{" +
                "string='" + string + '\'' +
                '}';
    }
}
