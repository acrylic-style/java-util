package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public interface ArgumentParsedResult {
    /**
     * Returns the argument map. <code>key=value</code> and <code>--key=value</code> are added to the map, but
     * <code>key</code> and <code>--key</code> are not.
     * @return the argument map
     */
    @Unmodifiable
    @NotNull
    Map<@NotNull String, @NotNull String> argumentMap();

    /**
     * Returns the short argument list. <code>-k</code> is added as <code>'k'</code>. If the argument was <code>-vvv</code>,
     * then the list would be like this: <code>['v', 'v', 'v']</code>. If the argument was <code>-k=v</code> , then the
     * list would be like this: <code>['k', '=', 'v']</code>.
     * @return the short argument list
     */
    @Unmodifiable
    @NotNull
    List<@NotNull Character> shortArguments();

    /**
     * Returns the unhandled argument list. If argument was <code>--a b</code>, then the list would contain
     * <code>["a", "b"]</code>. If the argument was <code>- --</code>, then the list would contain <code>["--", "-"]</code>.
     * @return the unhandled argument list
     */
    @Unmodifiable
    @NotNull
    List<@NotNull String> unhandledArguments();

    /**
     * Checks if {@link #argumentMap()} contains the provided <code>key</code>.
     * @param key the key to check
     * @return <code>true</code> if the key is contained, <code>false</code> otherwise
     */
    boolean containsArgumentKey(@NotNull String key);

    /**
     * Checks if {@link #argumentMap()} contains the provided <code>value</code>.
     * @param value the value to check
     * @return <code>true</code> if the value is contained, <code>false</code> otherwise
     */
    boolean containsArgumentValue(@Nullable String value);

    /**
     * Returns the value associated with the provided <code>key</code>.
     * @param key the key to get the value for
     * @return the value associated with the provided <code>key</code> or <code>null</code> if the key is not contained
     */
    @Nullable
    String getArgument(@NotNull String key);

    /**
     * Returns the value associated with the provided <code>key</code> or return <code>defaultValue</code> if the key is not contained.
     * This method never returns null if <code>defaultValue</code> is non-null value.
     * @param key the key to get the value for
     * @param defaultValue the default value to return if the key is not contained
     * @return the value associated with the provided <code>key</code> or <code>defaultValue</code> if the key is not contained
     */
    @Contract("_, !null -> !null")
    default String getArgumentOrDefault(@NotNull String key, @Nullable String defaultValue) {
        String value = getArgument(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Returns the value associated with the provided <code>key</code> or throw {@link IllegalArgumentException} if
     * key is not contained.
     * @param key the key to get the value for
     * @return the value associated with the provided <code>key</code>
     * @throws IllegalArgumentException if the key is not contained
     */
    @NotNull
    default String getArgumentOrThrow(@NotNull String key) {
        String value = getArgument(key);
        if (value == null) throw new IllegalArgumentException("Missing argument: " + key);
        return value;
    }

    /**
     * Checks if {@link #shortArguments()} contains the provided <code>key</code>.
     * @param key the key to check
     * @return <code>true</code> if the key is contained, <code>false</code> otherwise
     */
    boolean containsShortArgument(char key);

    /**
     * Checks if {@link #unhandledArguments()} contains the provided <code>key</code>.
     * @param key the key to check
     * @return <code>true</code> if the key is contained, <code>false</code> otherwise
     */
    boolean containsUnhandledArgument(@NotNull String key);
}
