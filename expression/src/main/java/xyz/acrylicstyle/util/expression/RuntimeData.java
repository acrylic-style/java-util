package xyz.acrylicstyle.util.expression;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuntimeData {
    private final Map<String, Object> variables;
    private final boolean allowPrivate;

    public RuntimeData(@NotNull Map<String, Object> variables, boolean allowPrivate) {
        this.variables = Collections.unmodifiableMap(variables);
        this.allowPrivate = allowPrivate;
    }

    public Object getVariable(@NotNull String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException(name + " is not defined");
        }
        return variables.get(name);
    }

    public boolean isAllowPrivate() {
        return allowPrivate;
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Object> variables = new HashMap<>();
        private boolean allowPrivate = false;

        private Builder() {}

        @Contract("_, _ -> this")
        public @NotNull Builder addVariable(@NotNull String name, Object value) {
            variables.put(name, value);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder allowPrivate(boolean allowPrivate) {
            this.allowPrivate = allowPrivate;
            return this;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull RuntimeData build() {
            return new RuntimeData(variables, allowPrivate);
        }
    }
}
