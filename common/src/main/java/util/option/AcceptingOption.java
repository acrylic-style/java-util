package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.function.BuiltinStringConverter;
import util.function.StringConverter;

public class AcceptingOption<T> implements Option<T> {
    @NotNull protected final String key;
    protected boolean required = false;
    protected T defaultValue = null;
    protected boolean requiredArg = false;
    protected String description = null;
    @NotNull protected StringConverter<T> converter;

    public AcceptingOption(@NotNull String key, @NotNull StringConverter<T> converter) {
        Validate.notNull(key, "key cannot be null");
        Validate.notNull(converter, "converter cannot be null");
        this.key = key;
        this.converter = converter;
    }

    @NotNull
    public final String getKey() { return key; }

    @Override
    public boolean isRequired() { return this.required; }

    @Override
    public @NotNull AcceptingOption<T> required() {
        this.required = true;
        return this;
    }

    @Override
    public boolean isRequiredArg() { return requiredArg; }

    @Override
    public @Nullable String getDescription() { return description; }

    @Override
    public AcceptingOption<T> description(@Nullable String description) {
        this.description = description;
        return this;
    }

    @Override
    public @NotNull AcceptingOption<T> withRequiredArg() {
        this.requiredArg = true;
        return this;
    }

    @Nullable
    public T getDefaultValue() { return this.defaultValue; }

    @Override
    public @NotNull AcceptingOption<T> defaultsTo(T t) {
        this.defaultValue = t;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E> AcceptingOption<E> ofType(Class<E> clazz) {
        StringConverter<E> converter = BuiltinStringConverter.findConverter(clazz);
        if (converter == null) throw new IllegalArgumentException("Could not find builtin converter by " + clazz.getCanonicalName() + " - try specifying StringConverter manually");
        return new DelegatingAcceptingOption<>((AcceptingOption<Object>) this, key, converter);
    }

    @SuppressWarnings("unchecked")
    public <E> AcceptingOption<E> ofType(@NotNull StringConverter<E> converter) {
        Validate.notNull(converter, "converter cannot be null");
        return new DelegatingAcceptingOption<>((AcceptingOption<Object>) this, key, converter);
    }

    @Override
    public @NotNull StringConverter<T> getConverter() { return converter; }
}
