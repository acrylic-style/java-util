package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.util.Deque;

public abstract class DummyInstruction extends Instruction {
    @Override
    public final Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        throw NoReturn.INSTANCE;
    }

    @Override
    public byte getId() {
        return (byte) 0xFF;
    }
}
