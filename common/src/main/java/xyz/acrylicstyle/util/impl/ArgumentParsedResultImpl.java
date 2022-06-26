package xyz.acrylicstyle.util.impl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.ArgumentParsedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ArgumentParsedResultImpl implements ArgumentParsedResult {
    private final Map<String, String> argumentMap;
    private final List<@NotNull Character> shortArguments;
    private final List<String> unhandledArguments;

    private ArgumentParsedResultImpl(@NotNull Map<String, String> argumentMap, @NotNull List<@NotNull Character> shortArguments, @NotNull List<String> unhandledArguments) {
        this.argumentMap = Collections.unmodifiableMap(argumentMap);
        this.shortArguments = Collections.unmodifiableList(shortArguments);
        this.unhandledArguments = Collections.unmodifiableList(unhandledArguments);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull String> argumentMap() {
        return argumentMap;
    }

    @Override
    public @NotNull List<@NotNull Character> shortArguments() {
        return shortArguments;
    }

    @Override
    public @NotNull List<@NotNull String> unhandledArguments() {
        return unhandledArguments;
    }

    @Override
    public boolean containsArgumentKey(@NotNull String key) {
        return argumentMap.containsKey(key);
    }

    @Override
    public boolean containsArgumentValue(@Nullable String value) {
        return argumentMap.containsValue(value);
    }

    @Override
    public @Nullable String getArgument(@NotNull String key) {
        return argumentMap.get(key);
    }

    @Override
    public boolean containsShortArgument(char key) {
        return shortArguments.contains(key);
    }

    @Override
    public boolean containsUnhandledArgument(@NotNull String key) {
        return unhandledArguments.contains(key);
    }

    @Override
    public String toString() {
        return "ArgumentParsedResultImpl{" +
                "argumentMap=" + argumentMap +
                ", shortArguments=" + shortArguments +
                ", unhandledArguments=" + unhandledArguments +
                '}';
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentParsedResultImpl)) return false;
        ArgumentParsedResultImpl that = (ArgumentParsedResultImpl) o;
        return argumentMap.equals(that.argumentMap) && shortArguments.equals(that.shortArguments) && unhandledArguments.equals(that.unhandledArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentMap, shortArguments, unhandledArguments);
    }

    static class Builder {
        private final Map<String, String> argumentMap = new HashMap<>();
        private final List<@NotNull Character> shortArguments = new ArrayList<>();
        private final List<String> unhandledArguments = new ArrayList<>();

        public boolean containsKey(@NotNull String key) {
            return argumentMap.containsKey(key);
        }

        public void put(@NotNull String key, @NotNull String value) {
            argumentMap.put(key, value);
        }

        public void addShort(char c) {
            shortArguments.add(c);
        }

        public void addUnhandled(@NotNull String argument) {
            unhandledArguments.add(argument);
        }

        public @NotNull ArgumentParsedResultImpl build() {
            return new ArgumentParsedResultImpl(argumentMap, shortArguments, unhandledArguments);
        }
    }
}
