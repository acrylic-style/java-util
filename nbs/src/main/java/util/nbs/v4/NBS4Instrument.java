package util.nbs.v4;

import org.jetbrains.annotations.NotNull;
import util.nbs.NBSInstrument;

public class NBS4Instrument implements NBSInstrument {
    @NotNull
    protected String name;

    @NotNull
    protected String sound;
    protected byte key;
    protected byte showKeyPress;

    public NBS4Instrument(@NotNull String name, @NotNull String sound, byte key, byte showKeyPress) {
        this.name = name;
        this.sound = sound;
        this.key = key;
        this.showKeyPress = showKeyPress;
    }

    @Override
    public String toString() {
        return "NBSVersion4Instrument{" +
                "name='" + name + '\'' +
                ", sound='" + sound + '\'' +
                ", key=" + key +
                ", showKeyPress=" + showKeyPress +
                '}';
    }

    @NotNull
    @Override
    public String getName() { return name; }

    @NotNull
    @Override
    public String getSound() { return sound; }

    @Override
    public byte getKey() { return key; }

    @Override
    public byte getShowKeyPress() { return showKeyPress; }
}
