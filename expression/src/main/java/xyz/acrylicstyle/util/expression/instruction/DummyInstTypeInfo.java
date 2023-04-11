package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;

public class DummyInstTypeInfo extends DummyInstruction {
    private final Class<?> clazz;

    public DummyInstTypeInfo(@NotNull Class<?> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<?> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "DummyInstTypeInfo{" +
                "clazz=" + clazz +
                '}';
    }
}
