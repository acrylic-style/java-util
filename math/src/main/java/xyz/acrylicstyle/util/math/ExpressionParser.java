package xyz.acrylicstyle.util.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

public class ExpressionParser {
    @Contract(pure = true)
    public static @NotNull Expression<Number> parse(@NotNull String s) throws InvalidArgumentException {
        StringReader reader = StringReader.create(s);
        Expression<Number> expression = parse(reader);
        reader.skipWhitespace();
        if (reader.hasNext()) {
            String token = reader.readToken();
            throw InvalidArgumentException.invalidToken(token).withContext(reader, -token.length(), token.length());
        }
        return expression;
    }

    @Contract(pure = true)
    public static @NotNull Expression<Number> parse(@NotNull StringReader reader) throws InvalidArgumentException {
        reader.skipWhitespace();
        char read = reader.peek();
        if (read == '(') {
            reader.skip();
            Expression<Number> expression = parse(reader);
            reader.skipWhitespace();
            read = reader.read();
            if (read != ')') {
                throw InvalidArgumentException.expected(')', read).withContext(reader);
            }
            return expression;
        } else {
            return parseExpression(reader);
        }
    }

    @Contract(pure = true)
    private static @NotNull Expression<Number> parseExpression(@NotNull StringReader reader) throws InvalidArgumentException {
        return parseNumber(reader);
    }

    @Contract("_ -> new")
    public static @NotNull SimpleNumberExpression<Number> parseNumber(@NotNull StringReader reader) throws InvalidArgumentException {
        reader.skipWhitespace();
        boolean negate = reader.peek() == '-';
        if (negate) {
            reader.skip();
        }
        reader.skipWhitespace();
        String strNumber = reader.readNumber();
        if (strNumber.isEmpty()) {
            throw InvalidArgumentException.expected("number", reader.readToken());
        }
        if (negate) {
            strNumber = "-" + strNumber;
        }
        char suffix = reader.hasNext() ? reader.peek() : ' ';
        Number number;
        if (suffix == 'L') {
            reader.skip();
            number = Long.parseLong(strNumber);
        } else if (suffix == 'F') {
            reader.skip();
            number = Float.parseFloat(strNumber);
        } else if (suffix == 'D') {
            reader.skip();
            number = Double.parseDouble(strNumber);
        } else {
            if (strNumber.contains(".")) {
                number = Double.parseDouble(strNumber);
            } else {
                number = Long.parseLong(strNumber);
            }
        }
        return new SimpleNumberExpression<>(number);
    }
}
