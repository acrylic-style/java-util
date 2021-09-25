package util.nbs;

import org.jetbrains.annotations.NotNull;

public interface NBSInstrument {
    /**
     * Returns the instrument name.
     * @return instrument name
     */
    @NotNull
    String getName();

    /**
     * Returns the custom sound id.
     * @return custom sound id
     */
    @NotNull
    String getSound();

    byte getKey();

    byte getShowKeyPress();
}
