package util.nbs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface NBSFile {
    /**
     * Returns header that contains tempos, layers, author etc.
     * @return the header
     */
    @NotNull
    NBSHeader getHeader();

    /**
     * Returns ticks data.
     * @return ticks data
     */
    @NotNull
    List<NBSTick> getTicks();

    /**
     * Returns last tick of the file. May be null if there are 0 ticks in the file.
     * @return last tick
     */
    @Nullable
    default NBSTick getLastTick() {
        List<NBSTick> sorted = getTicks().stream().sorted(Comparator.comparingInt(NBSTick::getStartingTick)).collect(Collectors.toList());
        if (sorted.size() == 0) return null;
        return sorted.get(sorted.size() - 1);
    }

    /**
     * Returns layers that holds information like layer name, volume, pitch etc.
     * @return the layer
     */
    @NotNull
    List<NBSLayerData> getLayers();

    /**
     * Returns custom instruments data. Not required if you don't use any custom instruments.
     * @return custom instruments
     */
    @NotNull
    List<NBSInstrument> getCustomInstruments();
}
