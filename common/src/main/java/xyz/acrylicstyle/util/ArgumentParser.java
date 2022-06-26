package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.impl.ArgumentParserImpl;

public interface ArgumentParser {
    @Contract(pure = true)
    @NotNull
    default ArgumentParsedResult parse(@NotNull String s) throws InvalidArgumentException {
        return parse(StringReader.create(s));
    }

    @Contract(pure = true)
    @NotNull
    ArgumentParsedResult parse(@NotNull StringReader reader) throws InvalidArgumentException;

    /**
     * Creates a new instance of {@link ArgumentParser} with default options.
     * @return the instance
     * @see ArgumentParserBuilder#builder()
     */
    @Contract("-> new")
    @NotNull
    static ArgumentParser create() {
        return new ArgumentParserImpl();
    }
}
