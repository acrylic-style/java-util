package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.function.BuiltinStringConverter;
import util.function.StringConverter;

public class DelegatingAcceptingOption<T> extends AcceptingOption<T> {
    private final AcceptingOption<Object> delegate;

    @SuppressWarnings("unchecked")
    DelegatingAcceptingOption(@NotNull AcceptingOption<Object> delegate, @NotNull String key, @NotNull StringConverter<T> converter) {
        super(key, converter);
        Validate.notNull(delegate, "delegate cannot be null");
        this.delegate = delegate;
        this.delegate.converter = (StringConverter<Object>) converter;
    }

    public AcceptingOption<Object> delegate() { return delegate; }

    @Override
    public @NotNull AcceptingOption<T> required() {
        delegate().required();
        return this;
    }

    @Override
    public boolean isRequired() { return delegate().isRequired(); }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable T getDefaultValue() {
        Object o = delegate().getDefaultValue();
        if (o instanceof String) {
            T t = converter.convert((String) o);
            delegate().defaultsTo(t);
            return t;
        }
        return (T) o;
    }

    @Override
    public @NotNull AcceptingOption<T> defaultsTo(T t) {
        delegate().defaultsTo(t);
        return this;
    }

    @Override
    public boolean isRequiredArg() {
        return delegate().isRequiredArg();
    }

    @Override
    public @NotNull AcceptingOption<T> withRequiredArg() {
        delegate().withRequiredArg();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull StringConverter<T> getConverter() {
        return (StringConverter<T>) delegate().getConverter();
    }

    @Override
    public AcceptingOption<T> description(@Nullable String description) {
        delegate().description(description);
        return this;
    }

    @Override
    public @Nullable String getDescription() {
        return delegate().getDescription();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> AcceptingOption<E> ofType(Class<E> clazz) {
        StringConverter<E> converter = BuiltinStringConverter.findConverter(clazz);
        if (converter == null) throw new IllegalArgumentException("Could not find builtin converter by " + clazz.getCanonicalName() + " - try specifying StringConverter manually");
        return new DelegatingAcceptingOption<>((AcceptingOption<Object>) this, key, converter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> AcceptingOption<E> ofType(@NotNull StringConverter<E> converter) {
        Validate.notNull(converter, "converter cannot be null");
        return new DelegatingAcceptingOption<>((AcceptingOption<Object>) this, key, converter);
    }
}
