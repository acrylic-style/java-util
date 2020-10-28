package util.jni;

import org.jetbrains.annotations.NotNull;
import util.io.Bytes;
import util.platform.LazyOSType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NativeCode {
    private boolean loaded = false;
    private final String name;

    public NativeCode(@NotNull String name) {
        LazyOSType os = LazyOSType.detectOS();
        if (os == LazyOSType.Mac_OS || os == LazyOSType.Mac_OS_X) name = "osx-" + name;
        if (os == LazyOSType.Windows) name = "windows-" + name;
        this.name = name;
    }

    public boolean load() throws UnsatisfiedLinkError {
        if (loaded) return true;
        String fullName = "java-util-all-" + name;
        try {
            System.loadLibrary(fullName);
            loaded = true;
            return true;
        } catch (Throwable ignore) {}
        try (InputStream soFile = NativeCode.class.getClassLoader().getResourceAsStream(name + ".so")) {
            if (soFile == null) throw new RuntimeException(new FileNotFoundException("Could not find " + name + ".so in jar file"));
            File temp = File.createTempFile(fullName, ".so");
            temp.deleteOnExit();
            OutputStream os = new FileOutputStream(temp);
            Bytes.copy(soFile, os);
            os.close();
            System.load(temp.getPath());
            loaded = true;
        } catch (IOException ignore) {}
        return loaded;
    }

    public static boolean loadLibrary(@NotNull String name) throws UnsatisfiedLinkError {
        return new NativeCode(name).load();
    }
}
