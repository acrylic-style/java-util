package util.agent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Objects;
import java.util.jar.JarFile;

public class JavaAgents implements Instrumentation {
    private static final JavaAgents DEFAULT_INSTANCE = new JavaAgents();
    private static JavaAgents instance = null;

    @Contract(pure = true)
    public static boolean isAvailable() {
        return getInstance() == instance;
    }

    @NotNull
    @Contract(pure = true)
    public static JavaAgents getInstance() {
        if (instance != null) return instance;
        return DEFAULT_INSTANCE;
    }

    public static void setInstance(@NotNull JavaAgents javaAgents) {
        Objects.requireNonNull(javaAgents);
        if (instance != null) throw new IllegalArgumentException("Cannot redefine JavaAgents singleton");
        instance = javaAgents;
    }

    public void addTransformer(ClassFileTransformer transformer) {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public boolean removeTransformer(ClassFileTransformer transformer) {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public boolean isRetransformClassesSupported() {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public boolean isRedefineClassesSupported() {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public boolean isModifiableClass(Class<?> theClass) {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public Class<?>[] getAllLoadedClasses() {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public Class<?>[] getInitiatedClasses(ClassLoader loader) {
        throw new IllegalArgumentException("Missing implementation");
    }

    public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
        throw new IllegalArgumentException("Missing implementation");
    }

    public long getObjectSize(Object object) {
        throw new IllegalArgumentException("Missing implementation");
    }

    public void appendToSystemClassLoaderSearch(JarFile jarFile) {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public boolean isNativeMethodPrefixSupported() {
        throw new IllegalArgumentException("Missing implementation");
    }

    @Override
    public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
        throw new IllegalArgumentException("Missing implementation");
    }

    public void appendToBootstrapClassLoaderSearch(JarFile jarFile) {
        throw new IllegalArgumentException("Missing implementation");
    }
}
