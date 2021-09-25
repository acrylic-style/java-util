package util.nbs;

import org.jetbrains.annotations.NotNull;

public interface NBSLayerData {
    /**
     * Returns the layer name.
     * @return layer name
     */
    @NotNull
    String getName();

    byte getLock();

    /**
     * Returns the volume of this layer.
     * @return volume
     */
    byte getVolume();

    byte getPanning();
}
