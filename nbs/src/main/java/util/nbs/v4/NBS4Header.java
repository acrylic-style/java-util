package util.nbs.v4;

import org.jetbrains.annotations.NotNull;
import util.nbs.NBSHeader;

public class NBS4Header implements NBSHeader {

    protected byte version;
    protected byte vanillaInstrumentCount;
    protected int songLength;
    protected int layers;
    protected String title;
    protected String author;
    protected String originalAuthor;
    protected String description;
    protected int tempo; //Divided by 100.
    protected byte autosave;
    protected byte autosaveDuration;
    protected byte timeSignature;
    protected long minutesSpent;
    protected long leftClicks;
    protected long rightClicks;
    protected long notesAdded;
    protected long notesRemoved;
    protected String midiSchemName;
    protected byte loopEnabled;
    protected byte maxLoops;
    protected int loopStartTick;

    protected NBS4Header() {}

    @Override
    public String toString() {
        return "NBSVersion4Header" +
                "\nversion=" + version +
                ",\n vanillaInstrumentCount=" + vanillaInstrumentCount +
                ",\n songLength=" + songLength +
                ",\n layers=" + layers +
                ",\n title='" + title + '\'' +
                ",\n author='" + author + '\'' +
                ",\n originalAuthor='" + originalAuthor + '\'' +
                ",\n description='" + description + '\'' +
                ",\n tempo=" + tempo +
                ",\n autosave=" + autosave +
                ",\n autosaveDuration=" + autosaveDuration +
                ",\n timeSignature=" + timeSignature +
                ",\n minutesSpent=" + minutesSpent +
                ",\n leftClicks=" + leftClicks +
                ",\n rightClicks=" + rightClicks +
                ",\n notesAdded=" + notesAdded +
                ",\n notesRemoved=" + notesRemoved +
                ",\n midiSchemName='" + midiSchemName + '\'' +
                ",\n loopEnabled=" + loopEnabled +
                ",\n maxLoops=" + maxLoops +
                ",\n loopStartTick=" + loopStartTick;
    }

    public byte getVersion() { return version; }

    @Override
    public byte getVanillaInstrumentCount() { return vanillaInstrumentCount; }

    @Override
    public int getSongLength() { return songLength; }

    @Override
    public int getLayers() { return layers; }

    @NotNull
    @Override
    public String getTitle() { return title; }

    @NotNull
    @Override
    public String getAuthor() { return author; }

    @NotNull
    @Override
    public String getOriginalAuthor() { return originalAuthor; }

    @NotNull
    @Override
    public String getDescription() { return description; }

    @Override
    public int getTempo() { return tempo; }

    @Override
    public byte getAutoSave() { return autosave; }

    @Override
    public byte getAutoSaveDuration() { return autosaveDuration; }

    @Override
    public byte getTimeSignature() { return timeSignature; }

    @Override
    public long getMinutesSpent() { return minutesSpent; }

    @Override
    public long getLeftClicks() { return leftClicks; }

    @Override
    public long getRightClicks() { return rightClicks; }

    @Override
    public long getNotesAdded() { return notesAdded; }

    @Override
    public long getNotesRemoved() { return notesRemoved; }

    @NotNull
    @Override
    public String getMidiSchemName() { return midiSchemName; }

    @Override
    public byte getLoopEnabled() { return loopEnabled; }

    @Override
    public byte getMaxLoops() { return maxLoops; }

    @Override
    public int getLoopStartTick() { return loopStartTick; }

    protected void setVersion(byte version) { this.version = version; }
    protected void setVanillaInstrumentCount(byte vanillaInstrumentCount) { this.vanillaInstrumentCount = vanillaInstrumentCount; }
    protected void setSongLength(int songLength) { this.songLength = songLength; }
    protected void setLayers(int layers) { this.layers = layers; }
    protected void setTitle(String title) { this.title = title; }
    protected void setAuthor(String author) { this.author = author; }
    protected void setOriginalAuthor(String originalAuthor) { this.originalAuthor = originalAuthor; }
    protected void setDescription(String description) { this.description = description; }
    protected void setTempo(int tempo) { this.tempo = tempo; }
    protected void setAutosave(byte autosave) { this.autosave = autosave; }
    protected void setAutosaveDuration(byte autosaveDuration) { this.autosaveDuration = autosaveDuration; }
    protected void setTimeSignature(byte timeSignature) { this.timeSignature = timeSignature; }
    protected void setMinutesSpent(long minutesSpent) { this.minutesSpent = minutesSpent; }
    protected void setLeftClicks(long leftClicks) { this.leftClicks = leftClicks; }
    protected void setRightClicks(long rightClicks) { this.rightClicks = rightClicks; }
    protected void setNotesAdded(long notesAdded) { this.notesAdded = notesAdded; }
    protected void setNotesRemoved(long notesRemoved) { this.notesRemoved = notesRemoved; }
    protected void setMidiSchemName(String midiSchemName) { this.midiSchemName = midiSchemName; }
    protected void setLoopEnabled(byte loopEnabled) { this.loopEnabled = loopEnabled; }
    protected void setMaxLoops(byte maxLoops) { this.maxLoops = maxLoops; }
    protected void setLoopStartTick(int loopStartTick) { this.loopStartTick = loopStartTick; }
}
