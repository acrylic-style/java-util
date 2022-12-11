package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.impl.StringReaderImpl;

import java.util.HashSet;
import java.util.Set;
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
     * @throws InvalidArgumentException if <code>index + 1</code> is out of bounds
     */
    default void skip() throws InvalidArgumentException {
        try {
            index(index() + 1);
        } catch (IndexOutOfBoundsException e) {
            throw InvalidArgumentException.createUnexpectedEOF().withContext(this);
        }
    }

    /**
     * Increments the <code>index</code> by <code>amount</code>.
     * @param amount the amount
     * @throws InvalidArgumentException if <code>index + amount</code> is out of bounds
     */
    default void skip(int amount) throws InvalidArgumentException {
        try {
            index(index() + amount);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidArgumentException("Unexpected EOF while skipping " + amount + " characters", e).withContext(this);
        }
    }

    /**
     * Returns the character at the current index and increments the <code>index</code> by 1.
     * @return a character
     * @throws InvalidArgumentException if <code>index + 1</code> is out of bounds
     */
    default char read() throws InvalidArgumentException {
        int i = index();
        skip();
        return content().charAt(i);
    }

    /**
     * Reads the all remaining characters.
     * @return a string
     */
    @NotNull
    default String readAll() {
        int index = index();
        index(content().length());
        return content().substring(index);
    }

    /**
     * Reads the non-whitespace and non-newline characters.
     * @return a string
     * @see #readQuotedString() for quoted strings
     */
    @NotNull
    default String readToken() {
        return readUntilIf(c -> !Character.isWhitespace(c) && c != '\n' && c != '\r');
    }

    /**
     * Skips all line terminators (<code>\n</code> and <code>\r</code>).
     * @return count of skipped characters
     */
    default int skipLineTerminators() throws InvalidArgumentException {
        if (isEOF()) {
            return 0;
        }
        int i = index();
        while (peek() == '\n' || peek() == '\r') {
            skip();
        }
        return index() - i;
    }

    /**
     * Returns the character at the current index and increments the <code>index</code> by 1. This method is equivalent
     * to <code>read(1)</code>.
     * @return a single character string
     * @throws InvalidArgumentException if <code>index + 1</code> is out of bounds
     */
    @NotNull
    default String readString() throws InvalidArgumentException {
        return read(1);
    }

    /**
     * Reads the string from <code>index</code> until <code>index + amount</code>.
     * @param amount the length of the string to read
     * @return the string
     * @throws InvalidArgumentException if <code>index + amount</code> is out of bounds
     */
    @NotNull
    default String read(int amount) throws InvalidArgumentException {
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
        if (isEOF()) return "";
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
        if (isEOF()) {
            return;
        }
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
     * @return count of skipped characters
     */
    default int skipWhitespace() {
        if (isEOF()) {
            return 0;
        }
        int index = this.index();
        skipUntilIf(Character::isWhitespace);
        return this.index() - index;
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
     * Reads a quoted string. Allows all escape sequences.
     * @return string, without surrounding quotes
     * @throws InvalidArgumentException if the string is not quoted
     * @see #readToken() for reading unquoted string
     * @see #readAll() for reading the rest of the content
     */
    @NotNull
    default String readQuotedString() throws InvalidArgumentException {
        return readQuotedString(false, (char[]) null);
    }

    /**
     * Reads a quoted string.
     * @param allowedEscapes set of allowed escape sequences. See {@link #readQuotedString(boolean, char...)} for valid characters.
     * @return string, without surrounding quotes
     * @throws InvalidArgumentException if the string is not quoted
     * @see #readToken() for reading unquoted string
     * @see #readAll() for reading the rest of the content
     */
    @NotNull
    default String readQuotedString(char @Nullable ... allowedEscapes) throws InvalidArgumentException {
        return readQuotedString(false, allowedEscapes);
    }

    /**
     * Reads a quoted string.
     * @param literalBackslash whether to interpret backslash as literal backslash. Effectively disables escape
     *                         sequences and makes <code>allowedEscapes</code> no-op.
     * @param allowedEscapes set of allowed escape sequences. Valid characters are: <code>['\n', '\t', '\r', '\\', '"']</code>
     * @return string, without surrounding quotes
     * @throws InvalidArgumentException if the string is not quoted
     * @see #readToken() for reading unquoted string
     * @see #readAll() for reading the rest of the content
     */
    @NotNull
    default String readQuotedString(boolean literalBackslash, char @Nullable ... allowedEscapes) throws InvalidArgumentException {
        if (peek() != '"') {
            throw InvalidArgumentException.expected('"', peek()).withContext(this);
        }
        Set<Character> escapes = new HashSet<>();
        if (allowedEscapes != null) {
            for (char c : allowedEscapes) {
                escapes.add(c);
            }
        }
        skip();
        StringBuilder sb = new StringBuilder();
        while (hasNext()) {
            char read = read();
            if (read == '"') {
                return sb.toString();
            } else if (read == '\\') {
                if (literalBackslash) {
                    sb.append(read); // add backslash
                    continue;
                }
                read = read();
                if (read == 'n' && (allowedEscapes == null || escapes.contains('\n'))) {
                    sb.append('\n');
                } else if (read == 't' && (allowedEscapes == null || escapes.contains('\t'))) {
                    sb.append('\t');
                } else if (read == 'r' && (allowedEscapes == null || escapes.contains('\r'))) {
                    sb.append('\r');
                } else if (read == '\\' && (allowedEscapes == null || escapes.contains('\\'))) {
                    sb.append('\\');
                } else if (read == '"' && (allowedEscapes == null || escapes.contains('"'))) {
                    sb.append('"');
                } else {
                    throw InvalidArgumentException.invalidEscape(read).withContext(this, -2, 2);
                }
            } else {
                sb.append(read);
            }
        }
        throw InvalidArgumentException.createUnexpectedEOF('"').withContext(this);
    }

    /**
     * Reads a quoted or unquoted string. If a string starts with double quotes, it will be read as a quoted string.
     * Allows all escape sequences when using {@link #readQuotedString()}.
     * @return a string
     * @throws InvalidArgumentException if the string is quoted and the content is not a valid string
     * @see #readQuotableString(char...) for reading a quoted string with allowed escape sequences
     */
    @NotNull
    default String readQuotableString() throws InvalidArgumentException {
        return readQuotableString(false, (char[]) null);
    }

    /**
     * Reads a quoted or unquoted string. If a string starts with double quotes, it will be read as a quoted string.
     * Passes the allowed escape sequences to {@link #readQuotedString(boolean, char...)}.
     * @param allowedEscapes set of allowed escape sequences. See {@link #readQuotedString(char[])} for valid characters.
     * @return a string
     * @throws InvalidArgumentException if the string is quoted and the content is not a valid string
     * @see #readQuotableString() for reading a quoted string with all escape sequences allowed
     */
    @NotNull
    default String readQuotableString(char @Nullable ... allowedEscapes) throws InvalidArgumentException {
        return readQuotableString(false, allowedEscapes);
    }

    /**
     * Reads a quoted or unquoted string. If a string starts with double quotes, it will be read as a quoted string.
     * Passes the allowed escape sequences to {@link #readQuotedString(boolean, char...)} )}.
     * @param literalBackslash whether to interpret backslash as literal backslash. Effectively disables escape
     *                         sequences and makes <code>allowedEscapes</code> no-op.
     * @param allowedEscapes set of allowed escape sequences. See {@link #readQuotedString(char[])} for valid characters.
     * @return a string
     * @throws InvalidArgumentException if the string is quoted and the content is not a valid string
     * @see #readQuotableString() for reading a quoted string with all escape sequences allowed
     */
    @NotNull
    default String readQuotableString(boolean literalBackslash, char @Nullable ... allowedEscapes) throws InvalidArgumentException {
        if (peek() == '"') {
            return readQuotedString(literalBackslash, allowedEscapes);
        } else {
            return readToken();
        }
    }

    /**
     * Reads a string until <code>c</code> is found.
     * @param c the character to stop at
     * @return a string
     */
    @NotNull
    default String readUntil(char c) {
        int originalIndex = index();
        int index = originalIndex;
        while (index < content().length() && content().charAt(index) != c) {
            index++;
        }
        index(index);
        return content().substring(originalIndex, index);
    }

    /**
     * Reads a string until one of <code>chars</code> is found.
     * @param chars set of characters to stop at
     * @return a string
     */
    @NotNull
    default String readUntil(char @NotNull ... chars) {
        Set<Character> set = new HashSet<>();
        for (char c : chars) {
            set.add(c);
        }
        int originalIndex = index();
        int index = originalIndex;
        while (index < content().length() && !set.contains(content().charAt(index))) {
            index++;
        }
        index(index);
        return content().substring(originalIndex, index);
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @NotNull
    @Override
    CharIterator iterator();

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
