package util.argument.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.StringReader;
import util.argument.InvalidArgumentException;

public interface Parser<T> {
    @Nullable
    T parse(@NotNull StringReader reader) throws InvalidArgumentException;
}
