package util.nbs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public interface NBSReader {
    /**
     * Returns what format version this reader supports.
     * @return the .nbs format version
     */
    List<Byte> getSupportedVersions();

    /**
     * Returns the byte order this reader will use.
     * @return byte order
     */
    @NotNull
    ByteOrder getFileByteOrder();

    /**
     * Fully reads .nbs file and returns an NBSFile. Implementation of this method should catch
     * {@link java.nio.BufferUnderflowException} and throw as {@link InvalidNBSBodyException}.
     * @param file the file
     * @return the loaded data
     * @throws InvalidNBSHeaderException if this reader does not support this format version
     * @throws InvalidNBSBodyException if the data contains invalid data that the reader does not support
     * @throws IOException if an I/O error occurs reading from the file
     * @throws OutOfMemoryError if the file is too large to read
     * @throws SecurityException if the current SecurityManager does not allow reading from the file
     */
    @NotNull
    NBSFile read(@NotNull File file) throws IOException;

    /**
     * Reads header from the {@link ByteBuffer} and returns NBSHeader.
     * @param buffer the byte buffer
     * @return newly created NBSHeader from the ByteBuffer
     */
    @NotNull
    NBSHeader readHeader(@NotNull ByteBuffer buffer);

    /**
     * Reads body from the {@link ByteBuffer} and returns NBSFile.
     * @param header the header, see {@link #readHeader(ByteBuffer)}
     * @param buffer the byte buffer
     * @return newly created NBSFile from the NBSHeader and ByteBuffer.
     */
    @NotNull
    NBSFile readBody(@NotNull NBSHeader header, @NotNull ByteBuffer buffer);
}
