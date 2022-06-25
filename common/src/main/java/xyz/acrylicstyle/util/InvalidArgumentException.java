package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class InvalidArgumentException extends Exception {
    private StringReader context;
    private int length = 1;

    public InvalidArgumentException() {
        super();
    }

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
        return new InvalidArgumentException("Encountered unexpected EOF");
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static InvalidArgumentException createUnexpectedEOF(char c) {
        return new InvalidArgumentException("Encountered unexpected EOF while looking for '" + c + "'");
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

    @Override
    public String toString() {
        if (context == null) {
            return super.toString();
        }
        try {
            StringBuilder sb = new StringBuilder(super.toString());
            String prev = context.peekWithAmount(-Math.min(context.index(), 10));
            String next = context.peekWithAmount(Math.min(context.readableCharacters(), Math.max(10, length)));
            int cursor = Math.min(10, context.index());
            sb.append("\n").append(prev).append(next);
            sb.append("\n").append(repeat(" ", cursor)).append("^").append(repeat("~", length - 1));
            return sb.toString();
        } catch (Throwable t) {
            return super.toString() + " [ERROR GENERATING MESSAGE: " + t.getClass().getTypeName() + ": " + t.getMessage() + "]: " + Arrays.toString(t.getStackTrace());
        }
    }

    private static @NotNull String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(s);
        return sb.toString();
    }
}
