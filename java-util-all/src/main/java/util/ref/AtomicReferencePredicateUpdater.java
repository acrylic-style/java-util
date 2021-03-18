package util.ref;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.magic.Magic;

import java.io.Serializable;
import java.util.function.BiPredicate;

public class AtomicReferencePredicateUpdater<V> implements Serializable {
    private static final long serialVersionUID = Magic.VERSION;
    private V value;
    private final BiPredicate<V, V> setterPredicate;
    @NotNull
    private String setterRejectedMessage = "";

    /**
     * Constructs FieldPredicateUpdater with predicate, and initial value.
     * @param setterPredicate the predicate to run when invoking {@link #set(Object)}.
     * @param initialValue initial value to set
     */
    @Contract(pure = true)
    public AtomicReferencePredicateUpdater(@NotNull BiPredicate<V, V> setterPredicate, @Nullable V initialValue) {
        Validate.notNull(setterPredicate, "predicate cannot be null");
        this.setterPredicate = setterPredicate;
        this.value = initialValue;
    }

    /**
     * Constructs FieldPredicateUpdater with predicate, and initial value.
     * @param setterPredicate the predicate to run when invoking {@link #set(Object)}.
     */
    @Contract(pure = true)
    public AtomicReferencePredicateUpdater(@NotNull BiPredicate<V, V> setterPredicate) {
        Validate.notNull(setterPredicate, "predicate cannot be null");
        this.setterPredicate = setterPredicate;
        this.value = null;
    }

    @NotNull
    public AtomicReferencePredicateUpdater<V> setterRejectedMessage(@NotNull String setterRejectedMessage) {
        this.setterRejectedMessage = setterRejectedMessage;
        return this;
    }

    @Contract(mutates = "this")
    public final void set(V newValue) {
        if (!setterPredicate.test(get(), newValue)) {
            throw new RejectedFieldOperationException("Field set '" + get() + "' to '" + newValue + "' was rejected" + getSetterRejectedMessage());
        }
        this.value = newValue;
    }

    @NotNull
    @Contract(pure = true)
    private String getSetterRejectedMessage() {
        if (setterRejectedMessage.length() == 0) return "";
        return " (" + setterRejectedMessage + ")";
    }

    public V get() {
        return value;
    }

    public boolean is(V value) { return value == null ? get() == null : value.equals(get()); }

    public boolean isNot(V value) { return !is(value); }
}
