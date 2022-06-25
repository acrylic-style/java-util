package xyz.acrylicstyle.util.impl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.StringReader;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class AbstractStringReader implements StringReader {
    private final @NotNull String content;

    protected AbstractStringReader(@NotNull String content) {
        Objects.requireNonNull(content, "content");
        this.content = content;
    }

    protected final void checkIndex(int index) {
        if (index < 0 || index > content.length()) {
            throw new IndexOutOfBoundsException("index: " + index + ", length: " + content.length() + " (expected 0 <= index <= length)");
        }
    }

    @Contract(pure = true)
    @Override
    public final @NotNull String content() {
        return content;
    }

    @Contract(value = " -> new", pure = true)
    @NotNull
    @Override
    public final Iterator<@NotNull Character> iterator() {
        return new Itr();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractStringReader)) return false;
        AbstractStringReader that = (AbstractStringReader) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    private class Itr implements Iterator<Character> {
        @Override
        public boolean hasNext() {
            return !isEOF();
        }

        @NotNull
        @Override
        public Character next() {
            if (isEOF()) {
                throw new NoSuchElementException("index: " + index() + ", length: " + content().length());
            }
            return read();
        }
    }
}
