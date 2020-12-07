package util.nbs.v4;

import util.nbs.NBSNote;

public class NBS4Note implements NBSNote {
    protected byte instrument;
    protected byte key;
    protected byte volume;
    protected byte panning;
    protected short pitch;

    public NBS4Note(byte instrument, byte key, byte volume, byte panning, short pitch) {
        this.instrument = instrument;
        this.key = key;
        this.volume = volume;
        this.panning = panning;
        this.pitch = pitch;
    }

    @Override
    public byte getInstrument() { return instrument; }

    @Override
    public byte getKey() { return key; }

    @Override
    public byte getVolume() { return volume; }

    @Override
    public byte getPanning() { return panning; }

    @Override
    public short getPitch() { return pitch; }

    @Override
    public String toString() {
        return "NBSVersion4Note{" +
                "instrument=" + instrument +
                ", key=" + key +
                ", volume=" + volume +
                ", panning=" + panning +
                ", pitch=" + pitch +
                '}';
    }
}
