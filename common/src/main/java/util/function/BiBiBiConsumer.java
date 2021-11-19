package util.function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface BiBiBiConsumer<A, B, C, D> {
    void accept(A a, B b, C c, D d);

    @NotNull
    default BiBiBiConsumer<A, B, C, D> andThen(@NotNull BiBiBiConsumer<? super A, ? super B, ? super C, ? super D> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d) -> {
            this.accept(a, b, c, d);
            after.accept(a, b, c, d);
        };
    }
}
