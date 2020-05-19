package java.lang.reflect;

import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.repository.ConstructorRepository;

import java.lang.annotation.Annotation;

public class RefExecutable extends Executable {
    private final Executable executable;

    public RefExecutable(Executable executable) {
        super();
        this.executable = executable;
    }

    @NotNull
    public Executable getExecutable() { return executable; }

    @Override
    public byte[] getAnnotationBytes() { return executable.getAnnotationBytes(); }

    @Override
    public Executable getRoot() { return executable.getRoot(); }

    @Override
    public boolean hasGenericInformation() { return executable.hasGenericInformation(); }

    @Override
    public ConstructorRepository getGenericInfo() { return executable.getGenericInfo(); }

    @Override
    public void specificToStringHeader(StringBuilder sb) { executable.specificToStringHeader(sb); }

    @Override
    public void specificToGenericStringHeader(StringBuilder sb) { executable.specificToStringHeader(sb); }

    @NotNull
    @Override
    public Class<?> getDeclaringClass() { return executable.getDeclaringClass(); }

    @Override
    public String getName() { return executable.getName(); }

    @Override
    public int getModifiers() { return executable.getModifiers(); }

    @Override
    public TypeVariable<?>[] getTypeParameters() { return executable.getTypeParameters(); }

    @Override
    public Class<?>[] getParameterTypes() { return executable.getParameterTypes(); }

    @Override
    public Class<?>[] getExceptionTypes() { return executable.getExceptionTypes(); }

    @Override
    public String toGenericString() { return executable.toGenericString(); }

    @Override
    public Annotation[][] getParameterAnnotations() {
        return executable.getParameterAnnotations();
    }

    @Override
    public void handleParameterNumberMismatch(int resultLength, int numParameters) {
        executable.handleParameterNumberMismatch(resultLength, numParameters);
    }

    @Override
    public AnnotatedType getAnnotatedReturnType() {
        return executable.getAnnotatedReturnType();
    }
}
