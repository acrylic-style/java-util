package util.nbs.v4;

import org.jetbrains.annotations.NotNull;
import util.nbs.NBSFile;
import util.nbs.NBSInstrument;
import util.nbs.NBSLayerData;
import util.nbs.NBSTick;
import util.nbs.NBSHeader;

import java.util.List;

public class NBS4File implements NBSFile {

    @NotNull
    protected NBSHeader header;

    @NotNull
    protected List<NBSTick> ticks;

    @NotNull
    protected List<NBSLayerData> layers;

    @NotNull
    protected List<NBSInstrument> customInstruments;

    public NBS4File(@NotNull NBSHeader header, @NotNull List<NBSTick> ticks, @NotNull List<NBSLayerData> layers, @NotNull List<NBSInstrument> customInstruments) {
        this.header = header;
        this.ticks = ticks;
        this.layers = layers;
        this.customInstruments = customInstruments;
    }

    @Override
    public @NotNull NBSHeader getHeader() { return header; }

    @Override
    public @NotNull List<NBSTick> getTicks() { return ticks; }

    @Override
    public @NotNull List<NBSLayerData> getLayers() { return layers; }

    @Override
    public @NotNull List<NBSInstrument> getCustomInstruments() { return customInstruments; }
}
