package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ArgumentParser;
import util.Validate;

public class OptionParserResult {
    @NotNull
    private final ArgumentParser result;

    public OptionParserResult(@NotNull ArgumentParser result) {
        Validate.notNull(result, "result cannot be null");
        this.result = result;
    }

    @Nullable
    public <T> T value(@NotNull AcceptingOption<T> option) {
        Validate.notNull(option, "option cannot be null");
        if (hasNoKey(option.getKey())) return option.getDefaultValue();
        T t = option.converter.convert(result.parsedRawOptions.get(option.getKey()));
        if (t == null) return option.getDefaultValue();
        return t;
    }

    public boolean has(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        return result.parsedOptions.containsKey(key) || result.arguments.contains(key);
    }

    public boolean hasKey(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        return result.parsedOptions.containsKey(key);
    }

    public boolean hasNoKey(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        return !result.parsedOptions.containsKey(key);
    }
}
