package util.argument.parser;

import org.jetbrains.annotations.NotNull;
import util.StringReader;
import util.argument.InvalidArgumentException;

public class LiteralParser implements Parser<String> {
    public static final LiteralParser INSTANCE = new LiteralParser();

    @NotNull
    @Override
    public String parse(@NotNull StringReader reader) throws InvalidArgumentException {
        if (reader.isEOF()) throw InvalidArgumentException.createStringDoesNotStartWith('"').withContext(reader);
        if (reader.readFirstAsChar() != '"') throw InvalidArgumentException.createStringDoesNotStartWith('"').withContext(reader);
        if (reader.isEOF()) throw InvalidArgumentException.createUnexpectedEOF('"').withContext(reader);
        StringBuilder s = new StringBuilder();
        while (true) {
            String read = reader.readFirst();
            if (read.equals("\"")) break;
            s.append(read);
            if (reader.isEOF()) throw InvalidArgumentException.createUnexpectedEOF('"').withContext(reader);
        }
        return s.toString();
    }
}
