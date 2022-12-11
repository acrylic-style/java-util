package xyz.acrylicstyle.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface CharIterator extends Iterator<@NotNull Character> {
    /**
     * @implNote This method returns the boxed value of {@link #nextChar()}.
     */
    @Override
    default @NotNull Character next() {
        return nextChar();
    }

    char nextChar();
}
