package util.io;

import java.io.PrintStream;

public class NoopPrintStream extends PrintStream {
    public NoopPrintStream() {
        super(new NoopOutputStream());
    }

    @Override
    public void print(double d) {}
}
