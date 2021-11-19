package util.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface BiBiConsumer<A, B, C> {
    void accept(A a, B b, C c);

    @NotNull
    default BiBiConsumer<A, B, C> andThen(@NotNull BiBiConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            this.accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
