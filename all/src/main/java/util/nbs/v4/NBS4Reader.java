package util.nbs.v4;

import org.jetbrains.annotations.NotNull;
import util.base.Bytes;
import util.nbs.InvalidNBSHeaderException;
import util.nbs.NBSFile;
import util.nbs.NBSHeader;
import util.nbs.NBSInstrument;
import util.nbs.NBSLayerData;
import util.nbs.NBSNote;
import util.nbs.NBSReader;
import util.nbs.NBSTick;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NBS4Reader implements NBSReader {
    @Override
    public List<Byte> getSupportedVersions() { return Bytes.asList(4, 5); }

    @Override
    public @NotNull ByteOrder getFileByteOrder() { return ByteOrder.LITTLE_ENDIAN; }

    @NotNull
    public NBSFile read(@NotNull File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(this.getFileByteOrder());
        NBSHeader header = this.readHeader(buffer);
        NBSFile nbsFile = this.readBody(header, buffer);
        buffer.clear();
        return nbsFile;
    }

    public @NotNull NBSHeader readHeader(@NotNull ByteBuffer buffer) {
        NBS4Header header = initialVersionChecks(buffer);
        return readHeaderPostCheck(buffer, header);
    }

    public NBS4Header initialVersionChecks(ByteBuffer buffer) {
        NBS4Header header = new NBS4Header();
        if (buffer.getShort() != 0) throw new InvalidNBSHeaderException("The specified file does not follow an modern *.NBS format.");
        byte version = buffer.get();
        if (!getSupportedVersions().contains(version)) throw new InvalidNBSHeaderException(String.format("The specified file (Version %s) does not use a supported NBS version.", version));
        header.setVersion(version);
        return header;
    }

    protected NBSHeader readHeaderPostCheck(ByteBuffer buffer, NBS4Header checkedHeader) {
        checkedHeader.setVanillaInstrumentCount(buffer.get());
        checkedHeader.setSongLength(readUnsignedShort(buffer));
        checkedHeader.setLayers(readUnsignedShort(buffer));
        checkedHeader.setTitle(readNBSString(buffer));
        checkedHeader.setAuthor(readNBSString(buffer));
        checkedHeader.setOriginalAuthor(readNBSString(buffer));
        checkedHeader.setDescription(readNBSString(buffer));
        checkedHeader.setTempo(readUnsignedShort(buffer));
        checkedHeader.setAutosave(buffer.get());
        checkedHeader.setAutosaveDuration(buffer.get());
        checkedHeader.setTimeSignature(buffer.get());
        checkedHeader.setMinutesSpent(readUnsignedInt(buffer));
        checkedHeader.setLeftClicks(readUnsignedInt(buffer));
        checkedHeader.setRightClicks(readUnsignedInt(buffer));
        checkedHeader.setNotesAdded(readUnsignedInt(buffer));
        checkedHeader.setNotesRemoved(readUnsignedInt(buffer));
        checkedHeader.setMidiSchemName(readNBSString(buffer));
        checkedHeader.setLoopEnabled(buffer.get());
        checkedHeader.setMaxLoops(buffer.get());
        checkedHeader.setLoopStartTick(readUnsignedShort(buffer));
        return checkedHeader;
    }

    public @NotNull NBSFile readBody(@NotNull NBSHeader header, @NotNull ByteBuffer buffer) {
        List<NBSTick> ticks = readNotes(header.getLayers(), buffer);
        List<NBSLayerData> layers = Arrays.asList(readDetailedLayerDataEntries(header.getLayers(), buffer));
        List<NBSInstrument> customInstruments = Arrays.asList(readInstrumentDataEntries(buffer));

        return new NBS4File(header, ticks, layers, customInstruments);
    }

    protected ArrayList<NBSTick> readNotes(int layers, ByteBuffer buffer) {
        int currentTick = -1;
        ArrayList<NBSTick> ticks = new ArrayList<>();

        while (true) {
            int tickJumps = readUnsignedShort(buffer);
            if (tickJumps == 0) break;
            currentTick += tickJumps;
            List<NBSNote> notes = Arrays.asList(readTickNoteLayers(layers, buffer));
            ticks.add(new NBS4Tick(currentTick, notes));
        }
        return ticks;
    }

    protected NBSNote[] readTickNoteLayers(int layers, ByteBuffer buffer) {
        NBSNote[] notes = new NBSNote[layers];
        int currentLayer = -1;
        while (true) {
            int layerJumps = readUnsignedShort(buffer);
            if (layerJumps == 0) break;
            currentLayer += layerJumps;
            NBSNote note = readNote(buffer);
            notes[currentLayer] = note;
        }
        return notes;
    }

    protected NBSNote readNote(ByteBuffer buffer) {
        byte instrument = buffer.get();
        byte key = buffer.get();
        byte volume = buffer.get();
        byte panning = buffer.get();
        short pitch = buffer.getShort();
        return new NBS4Note(instrument, key, volume, panning, pitch);
    }

    protected NBSLayerData[] readDetailedLayerDataEntries(int layers, ByteBuffer buffer) {
        NBSLayerData[] data = new NBSLayerData[layers];
        for (int i = 0; i < layers; i++){
            data[i] = readDetailedLayerData(buffer);
        }
        return data;
    }

    protected NBSLayerData readDetailedLayerData(ByteBuffer buffer) {
        String name = readNBSString(buffer);
        byte lock = buffer.get();
        byte volume = buffer.get();
        byte panning = buffer.get();
        return new NBS4LayerData(name, lock, volume, panning);
    }

    protected NBSInstrument[] readInstrumentDataEntries(ByteBuffer buffer) {
        byte instrumentCount = buffer.get();
        NBSInstrument[] data = new NBSInstrument[instrumentCount];
        for (int i = 0; i < data.length; i++){
            data[i] = readInstrumentData(buffer);
        }
        return data;
    }

    protected NBSInstrument readInstrumentData(ByteBuffer buffer) {
        String name = readNBSString(buffer);
        String sound = readNBSString(buffer);
        byte key = buffer.get();
        byte showKeyPress = buffer.get();
        return new NBS4Instrument(name, sound, key, showKeyPress);
    }

    protected static int readUnsignedShort(ByteBuffer buffer){
        byte[] bytes = new byte[2];
        buffer.get(bytes);

        int integerVal = 0;
        for (int i = bytes.length - 1; i >= 0; i--){
            byte b = bytes[i];
            integerVal += Byte.toUnsignedInt(b) << (i * 8);
        }
        return integerVal;
    }

    protected static long readUnsignedInt(ByteBuffer buffer){
        byte[] bytes = new byte[4];
        buffer.get(bytes);

        long longVal = 0;
        for (int i = bytes.length - 1; i >= 0; i--){
            byte b = bytes[i];
            longVal += (long) Byte.toUnsignedInt(b) << (i * 8);
        }
        return longVal;
    }

    protected static String readNBSString(ByteBuffer buffer){
        int length = (int) readUnsignedInt(buffer);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        char[] string = new char[length];
        charBuffer.get(string);
        return String.valueOf(string);
    }

    protected static byte flipByteBitOrder(byte flipByte){
        return (byte) (Integer.reverse(flipByte) >>> (Integer.SIZE - Byte.SIZE));
    }
}
