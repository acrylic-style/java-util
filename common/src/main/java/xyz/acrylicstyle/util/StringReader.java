package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.impl.StringReaderImpl;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Reads characters from a string.
 */
public interface StringReader extends Iterable<@NotNull Character> {
    /**
     * Returns the original content of the reader.
     * @return the content
     */
    @Contract(pure = true)
    @NotNull
    String content();

    /**
     * Returns the current index.
     * @return the current index
     */
    @Contract(pure = true)
    int index();

    /**
     * Sets the current index.
     * @param index the new index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    void index(int index);

    /**
     * Returns the character at the current index.
     * @return a character
     */
    default char peek() {
        return content().charAt(index());
    }

    /**
     * Returns the character at the current index + offset.
     * @param offset the offset
     * @return a character
     */
    default char peek(int offset) {
        return content().charAt(index() + offset);
    }

    /**
     * Returns the string at the between the current index and the index + amount.
     * @param amount the amount to read
     * @return a string
     * @throws IndexOutOfBoundsException if the index + amount is out of bounds
     */
    @NotNull
    default String peekWithAmount(int amount) {
        if (amount < 0) {
            return content().substring(index() + amount, index());
        }
        return content().substring(index(), index() + amount);
    }

    /**
     * Increments the <code>index</code> by 1.
     * @throws IndexOutOfBoundsException if <code>index + 1</code> is out of bounds
     */
    default void skip() {
        index(index() + 1);
    }

    /**
     * Increments the <code>index</code> by <code>amount</code>.
     * @param amount the amount
     * @throws IndexOutOfBoundsException if <code>index + amount</code> is out of bounds
     */
    default void skip(int amount) {
        index(index() + amount);
    }

    /**
     * Returns the character at the current index and increments the <code>index</code> by 1.
     * @return a character
     * @throws IndexOutOfBoundsException if <code>index + 1</code> is out of bounds
     */
    default char read() {
        int i = index();
        skip(1);
        return content().charAt(i);
    }

    /**
     * Reads the all remaining characters.
     * @return a string
     */
    @Contract(pure = true)
    @NotNull
    default String readAll() {
        int index = index();
        index(content().length() - 1);
        return content().substring(index);
    }

    /**
     * Reads the non-whitespace characters.
     * @return a string
     */
    @NotNull
    default String readToken() {
        return readUntilIf(c -> !Character.isWhitespace(c));
    }

    /**
     * Returns the character at the current index and increments the <code>index</code> by 1. This method is equivalent
     * to <code>read(1)</code>.
     * @return a single character string
     * @throws IndexOutOfBoundsException if <code>index + 1</code> is out of bounds
     */
    @NotNull
    default String readString() {
        return read(1);
    }

    /**
     * Reads the string from <code>index</code> until <code>index + amount</code>.
     * @param amount the length of the string to read
     * @return the string
     * @throws IndexOutOfBoundsException if <code>index + amount</code> is out of bounds
     */
    @NotNull
    default String read(int amount) {
        int index = this.index();
        this.skip(amount);
        return content().substring(index, index + amount);
    }

    /**
     * Reads the string until the end of the content or until predicate returns false.
     * @param predicate the predicate
     * @return the string
     */
    @NotNull
    default String readUntilIf(@NotNull Predicate<@NotNull Character> predicate) {
        int originalIndex = index();
        int index = originalIndex;
        while (index < content().length() && predicate.test(content().charAt(index))) {
            index++;
        }
        index(index);
        return content().substring(originalIndex, index);
    }

    /**
     * Skips the string until the end of the content or until predicate returns false.
     */
    default void skipUntilIf(@NotNull Predicate<@NotNull Character> predicate) {
        int index = this.index();
        while (index < content().length() && predicate.test(content().charAt(index))) {
            index++;
        }
        this.index(index);
    }

    /**
     * Reads the number, basically iterates the characters until end of digit or end of the content.
     * @return the number (in string)
     */
    @NotNull
    default String readNumber() {
        return readUntilIf(c -> c >= '0' && c <= '9');
    }

    /**
     * Skips all whitespace characters until non-whitespace character is found.
     * @return <code>true</code> if at least one whitespace character was found, <code>false</code> otherwise
     */
    default boolean skipWhitespace() {
        int index = this.index();
        skipUntilIf(Character::isWhitespace);
        return index != this.index();
    }

    /**
     * Checks if the current index is at the end of the content.
     * @return <code>true</code> if the current index is at the end of the content; <code>false</code> otherwise
     */
    default boolean isEOF() {
        return index() >= content().length();
    }

    /**
     * Checks if the reader is not at the end of the content.
     * @return <code>true</code> if the reader is not at the end of the content; <code>false</code> otherwise
     */
    default boolean hasNext() {
        return !isEOF();
    }

    /**
     * Returns the length of readable characters.
     * @return the length
     */
    default int readableCharacters() {
        return content().length() - index();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @NotNull
    @Override
    Iterator<@NotNull Character> iterator();

    /**
     * Creates a new {@link StringReader} with same content and same index.
     * @return {@link StringReader}
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    StringReader copy();

    /**
     * Creates a new {@link StringReader} from the given content. Returned implementation is not thread-safe.
     * @param content the content
     * @return a new {@link StringReader}
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull StringReader create(@NotNull String content) {
        return new StringReaderImpl(content);
    }

    /**
     * Creates a new {@link StringReader} from the given content and the initial index. Returned implementation is not
     * thread-safe.
     * @param content the content
     * @param index the initial index
     * @return a new {@link StringReader}
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull StringReader create(@NotNull String content, int index) {
        return new StringReaderImpl(content, index);
    }
}
