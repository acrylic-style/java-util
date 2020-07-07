package util;

import java.util.Objects;

public interface BiBiBiConsumer<A, B, C, D> {
    void accept(A a, B b, C c, D d);

    default BiBiBiConsumer<A, B, C, D> andThen(BiBiBiConsumer<? super A, ? super B, ? super C, ? super D> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d) -> {
            this.accept(a, b, c, d);
            after.accept(a, b, c, d);
        };
    }
}
