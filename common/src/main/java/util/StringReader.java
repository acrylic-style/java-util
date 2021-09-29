package util;

import org.jetbrains.annotations.NotNull;

public class StringReader {
    private final String text;
    private int index = 0;

    public StringReader(@NotNull String text) {
        Validate.notNull(text, "text cannot be null");
        this.text = text;
    }

    @NotNull
    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public StringReader setIndex(int index) {
        if (index >= text.length() || index <= -text.length()) {
            throw new IndexOutOfBoundsException("Index: " + index + " >= Size: " + text.length());
        }
        this.index = index;
        return this;
    }

    /**
     * Reads the next character. Does not update the current index.
     */
    public char peek() {
        int idx;
        if (index >= 0) {
            idx = index;
        } else {
            idx = text.length() - 1 - index;
        }
        return text.charAt(idx);
    }

    /**
     * Reads the remaining characters. Does not update the current index.
     */
    @NotNull
    public String peekRemaining() {
        int end;
        if (index < 0) {
            end = -index;
        } else {
            end = text.length();
        }
        return text.substring(index, end);
    }

    /**
     * Reads the first character from text. Updates the index by 1.
     * @return read string
     * @throws IllegalArgumentException if index is negative
     */
    @NotNull
    public String readFirst() {
        return read(1);
    }

    /**
     * Reads the text by provided amount. Updates the index by `amount`.
     * @param amount the amount to read text
     * @return read string
     * @throws IllegalArgumentException if index is negative
     */
    @NotNull
    public String read(int amount) {
        if (index < 0) throw new IllegalArgumentException("Index is negative");
        String substr = text.substring(index, index + amount);
        index += amount;
        return substr;
    }

    /**
     * Checks if the remaining text starts with `prefix`.
     * @param prefix the prefix
     */
    public boolean startsWith(String prefix) {
        return peekRemaining().startsWith(prefix);
    }

    /**
     * Updates the current index by `amount`.
     */
    @NotNull
    public StringReader skip(int amount) {
        this.index += amount;
        return this;
    }

    /**
     * Checks if the reader has encountered EOF.
     */
    public boolean isEOF() {
        return index >= text.length();
    }
}
