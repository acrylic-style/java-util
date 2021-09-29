package util.nbs;

public interface NBSNote {
    /**
     * Returns instrument as byte.
     * @return instrument
     */
    byte getInstrument();

    byte getKey();

    /**
     * Returns the volume of this note.
     * @return volume
     */
    byte getVolume();

    byte getPanning();

    /**
     * Returns the pitch of this note.
     * @return pitch
     */
    short getPitch();
}
