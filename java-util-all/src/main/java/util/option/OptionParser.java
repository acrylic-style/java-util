package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ArgumentParser;
import util.ICollectionList;
import util.Validate;
import util.function.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class OptionParser {
    private final List<AcceptingOption<?>> options = new ArrayList<>();

    @NotNull
    public AcceptingOption<String> accepts(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        AcceptingOption<String> option = new AcceptingOption<>(key, StringConverter.identify());
        options.add(option);
        return option;
    }

    @NotNull AcceptingOption<String> accepts(@NotNull String key, @Nullable String description) {
        return accepts(key).description(description);
    }

    @NotNull
    public OptionParserResult parse(String... args) {
        OptionParserResult result = new OptionParserResult(new ArgumentParser(ICollectionList.asList(args).join(" ")));
        ICollectionList.asList(options).filter(Option::isRequired).forEach(option -> {
            String description = option.description == null ? "" : " (" + option.description + ")";
            if (option.isRequiredArg()) {
                if (result.hasNoKey(option.key)) throw new RuntimeException("Option with required arg '" + option.key + "' does not exist!" + description);
            } else {
                if (!result.has(option.key)) throw new RuntimeException("Option '" + option.key + "' does not exist!" + description);
            }
        });
        return result;
    }
}
