package util.nbs;

import org.jetbrains.annotations.NotNull;

public interface NBSHeader {
    default float getAdjustedTempo() { return getTempo() / 100F; }

    /**
     * Returns format version. (.nbs file format version)
     * @return format version
     */
    byte getVersion();

    /**
     * Returns vanilla instrument count.
     * @return instrument count
     */
    byte getVanillaInstrumentCount();

    /**
     * Returns the length of this song.
     * @return length
     */
    int getSongLength();

    /**
     * Returns how many layers this file have.
     * @return layer count
     */
    int getLayers();

    /**
     * Returns the title of this file.
     * @return title
     */
    @NotNull
    String getTitle();

    /**
     * Returns the author of this file.
     * @return author
     */
    @NotNull
    String getAuthor();

    /**
     * Returns the original author of this file.
     * @return original author
     */
    @NotNull
    String getOriginalAuthor();

    /**
     * Returns the description of this file.
     * @return description
     */
    @NotNull
    String getDescription();

    /**
     * Returns the tempo. Divided by 100.
     * @return tempo
     */
    int getTempo();

    byte getAutoSave();

    byte getAutoSaveDuration();

    byte getTimeSignature();

    long getMinutesSpent();

    long getLeftClicks();

    long getRightClicks();

    long getNotesAdded();

    long getNotesRemoved();

    @NotNull
    String getMidiSchemName();

    byte getLoopEnabled();

    byte getMaxLoops();

    int getLoopStartTick();
}
