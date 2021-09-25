package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.function.StringConverter;

public interface Option<T> {
    @NotNull
    String getKey();

    boolean isRequired();

    default boolean isNotRequired() { return !isRequired(); }

    @NotNull
    Option<T> required();

    boolean isRequiredArg();

    @NotNull
    Option<T> withRequiredArg();

    @Nullable
    T getDefaultValue();

    @NotNull
    Option<T> defaultsTo(@Nullable T t);

    @Nullable
    String getDescription();

    Option<T> description(@Nullable String description);

    StringConverter<T> getConverter();
}
