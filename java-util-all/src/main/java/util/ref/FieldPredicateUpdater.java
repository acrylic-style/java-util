package util.ref;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.magic.Magic;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;

public class FieldPredicateUpdater<V> implements Serializable {
    private static final long serialVersionUID = Magic.VERSION;
    private final AtomicReference<V> reference;
    private final BiPredicate<V, V> setterPredicate;
    @NotNull
    private String setterRejectedMessage = "";

    /**
     * Constructs FieldPredicateUpdater with predicate, and initial value.
     * @param setterPredicate the predicate to run when invoking {@link #set(Object)}.
     * @param initialValue initial value to set
     */
    @Contract(pure = true)
    public FieldPredicateUpdater(@NotNull BiPredicate<V, V> setterPredicate, @Nullable V initialValue) {
        Validate.notNull(setterPredicate, "predicate cannot be null");
        this.setterPredicate = setterPredicate;
        this.reference = new AtomicReference<>(initialValue);
    }

    /**
     * Constructs FieldPredicateUpdater with predicate, and initial value.
     * @param setterPredicate the predicate to run when invoking {@link #set(Object)}.
     */
    @Contract(pure = true)
    public FieldPredicateUpdater(@NotNull BiPredicate<V, V> setterPredicate) {
        Validate.notNull(setterPredicate, "predicate cannot be null");
        this.setterPredicate = setterPredicate;
        this.reference = new AtomicReference<>();
    }

    @NotNull
    public FieldPredicateUpdater<V> setterRejectedMessage(@NotNull String setterRejectedMessage) {
        this.setterRejectedMessage = setterRejectedMessage;
        return this;
    }

    @Contract(mutates = "this")
    public final void set(V newValue) {
        if (!setterPredicate.test(get(), newValue)) {
            throw new RejectedFieldOperationException("Field set '" + get() + "' to '" + newValue + "' was rejected" + getSetterRejectedMessage());
        }
        this.reference.set(newValue);
    }

    @NotNull
    @Contract(pure = true)
    private String getSetterRejectedMessage() {
        if (setterRejectedMessage.length() == 0) return "";
        return " (" + setterRejectedMessage + ")";
    }

    public V get() { return reference.get(); }

    public boolean is(V value) { return value == null ? get() == null : value.equals(get()); }

    public boolean isNot(V value) { return !is(value); }
}
