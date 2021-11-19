package util.maven;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageReporter {
    void onError(@NotNull String message, @Nullable Throwable throwable);
}
