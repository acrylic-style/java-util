package xyz.acrylicstyle.util.impl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.StringReader;

import java.util.Objects;

public class StringReaderImpl extends AbstractStringReader {
    private int index;

    /**
     * Creates a new StringReader with the given content.
     * @param content the content
     */
    @Contract(pure = true)
    public StringReaderImpl(@NotNull String content) {
        super(content);
    }

    /**
     * Creates a new StringReader with the given content and index.
     * @param content the content
     * @param index the initial index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Contract(pure = true)
    public StringReaderImpl(@NotNull String content, int index) {
        super(content);
        checkIndex(index);
        this.index = index;
    }

    @Override
    public void index(int index) {
        checkIndex(index);
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public @NotNull StringReader copy() {
        return new StringReaderImpl(content(), index);
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringReaderImpl)) return false;
        if (!super.equals(o)) return false;
        StringReaderImpl that = (StringReaderImpl) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index);
    }
}
