package util;

import java.util.Objects;

public interface BiBiConsumer<A, B, C> {
    void accept(A a, B b, C c);

    default BiBiConsumer<A, B, C> andThen(BiBiConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            this.accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
