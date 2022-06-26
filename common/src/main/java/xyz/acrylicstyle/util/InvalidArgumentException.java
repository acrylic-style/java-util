package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * An exception thrown when an argument is invalid. {@link #toString()} outputs the multiple lines when context is provided.
 */
public class InvalidArgumentException extends Exception {
    public static final int SHOW_BEFORE_AFTER = 15;
    private StringReader context;
    private int length = 1;
    private String toString = null;

    /*
    public InvalidArgumentException() {
        super();
    }
    */

    public InvalidArgumentException(@Nullable String message) {
        super(message);
    }

    public InvalidArgumentException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentException(@Nullable Throwable cause) {
        super(cause);
    }

    @Contract("_ -> new")
    public static @NotNull InvalidArgumentException invalidToken(char token) {
        return new InvalidArgumentException("Invalid token: '" + token + "'");
    }

    @Contract("_ -> new")
    public static @NotNull InvalidArgumentException invalidToken(@NotNull String token) {
        return new InvalidArgumentException("Invalid token: " + token);
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static InvalidArgumentException expected(char c) {
        return new InvalidArgumentException("Expected '" + c + "'");
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static InvalidArgumentException expected(char c, char actual) {
        return new InvalidArgumentException("Expected '" + c + "', but got: '" + actual + "'");
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static InvalidArgumentException expected(@NotNull String what, @NotNull String actual) {
        return new InvalidArgumentException("Expected " + what + ", but got: '" + actual + "'");
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static InvalidArgumentException createUnexpectedEOF() {
        return new InvalidArgumentException("Unexpected EOF");
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static InvalidArgumentException createUnexpectedEOF(char c) {
        return new InvalidArgumentException("Unexpected EOF while looking for '" + c + "'");
    }

    @Contract("_ -> new")
    public static @NotNull InvalidArgumentException invalidEscape(char c) {
        return new InvalidArgumentException("Invalid escape sequence: '\\" + c + "'");
    }

    @NotNull
    public InvalidArgumentException withContext(@NotNull StringReader reader) {
        return withContext(reader, 0, 1);
    }

    @NotNull
    public InvalidArgumentException withContext(@NotNull StringReader reader, int offset, int length) {
        this.context = reader.copy();
        this.context.index(Math.min(this.context.content().length(), Math.max(0, this.context.index() + offset)));
        this.length = Math.max(1, length);
        return this;
    }

    @Nullable
    public StringReader getContext() {
        return context;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }
        if (context == null) {
            return toString = super.toString();
        }
        try {
            StringBuilder sb = new StringBuilder(super.toString());
            String prev = context.peekWithAmount(-Math.min(context.index(), SHOW_BEFORE_AFTER));
            String next = context.peekWithAmount(Math.min(context.readableCharacters(), Math.max(SHOW_BEFORE_AFTER, length)));
            int cursor = Math.min(SHOW_BEFORE_AFTER, context.index());
            sb.append("\n").append(prev).append(next);
            sb.append("\n").append(repeat(" ", cursor)).append("^").append(repeat("~", length - 1));
            return toString = sb.toString();
        } catch (Throwable t) {
            return toString = super.toString() + " [ERROR GENERATING MESSAGE: " + t.getClass().getTypeName() + ": " + t.getMessage() + "]: " + Arrays.toString(t.getStackTrace());
        }
    }

    private static @NotNull String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(s);
        return sb.toString();
    }
}
