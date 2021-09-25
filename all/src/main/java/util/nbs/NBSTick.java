package util.nbs;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NBSTick {
    /**
     * Returns tick where this tick is located at.
     * @return the tick
     */
    int getStartingTick();

    /**
     * Get notes on each layers.
     * @return the notes
     */
    @NotNull
    List<NBSNote> getLayers();
}
