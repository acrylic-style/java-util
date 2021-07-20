package util.io;

import java.io.OutputStream;

public class NoopOutputStream extends OutputStream {
    @Override
    public void write(int b) {}
}
