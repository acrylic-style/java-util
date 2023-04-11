package xyz.acrylicstyle.util.expression;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompileData {
    private final Map<String, Class<?>> variables;

    public CompileData(@NotNull Map<String, Class<?>> variables) {
        this.variables = Collections.unmodifiableMap(variables);
    }

    public @NotNull Class<?> getVariable(@NotNull String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException(name + " is not defined");
        }
        return Objects.requireNonNull(variables.get(name));
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Class<?>> variables = new HashMap<>();

        private Builder() {}

        @Contract("_, _ -> this")
        public @NotNull Builder addVariable(@NotNull String name, @NotNull Class<?> value) {
            variables.put(name, Objects.requireNonNull(value));
            return this;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull CompileData build() {
            return new CompileData(variables);
        }
    }
}
