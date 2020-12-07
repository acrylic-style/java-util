package util.nbs;

import org.jetbrains.annotations.NotNull;
import java.util.List;

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
