package util.reflect;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.NoSuchElementException;

public class ModifierEditor<T> {
    @NotNull private final Class<?> clazz;
    @NotNull private final T instance;

    ModifierEditor(@NotNull final Class<T> clazz, @NotNull T instance) {
        try {
            clazz.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            throw new NoSuchElementException("This class does not have modifiers field");
        }
        this.clazz = clazz;
        this.instance = instance;
    }

    @NotNull
    public T getInstance() { return instance; }

    @NotNull
    public ModifierEditor<T> getModifierEditor() {
        return this;
    }

    @NotNull
    @SneakyThrows({NoSuchFieldException.class})
    public ModifierEditor<T> setModifier(int modifier) {
        new RefField<>(clazz.getDeclaredField("modifiers")).accessible(true).setInt(instance, modifier);
        return this;
    }

    @SneakyThrows({NoSuchFieldException.class})
    public int getModifier() {
        return new RefField<>(clazz.getDeclaredField("modifiers")).accessible(true).getInt(instance);
    }

    @NotNull
    public Class<?> getClazz() { return clazz; }

    @NotNull
    public ModifierEditor<T> addFinal() { return setModifier(getModifier() & Modifier.FINAL); }

    @NotNull
    public ModifierEditor<T> removeFinal() { return setModifier(getModifier() & ~Modifier.FINAL); }

    @NotNull
    public ModifierEditor<T> addSynchronized() { return setModifier(getModifier() & Modifier.SYNCHRONIZED); }

    @NotNull
    public ModifierEditor<T> removeSynchronized() { return setModifier(getModifier() & ~Modifier.SYNCHRONIZED); }

    @NotNull
    public ModifierEditor<T> addStrict() { return setModifier(getModifier() & Modifier.STRICT); }

    @NotNull
    public ModifierEditor<T> removeStrict() { return setModifier(getModifier() & ~Modifier.STRICT); }

    @NotNull
    public ModifierEditor<T> addVolatile() { return setModifier(getModifier() & Modifier.VOLATILE); }

    @NotNull
    public ModifierEditor<T> removeVolatile() { return setModifier(getModifier() & ~Modifier.VOLATILE); }

    @NotNull
    public ModifierEditor<T> addTransient() { return setModifier(getModifier() & Modifier.TRANSIENT); }

    @NotNull
    public ModifierEditor<T> removeTransient() { return setModifier(getModifier() & ~Modifier.TRANSIENT); }

    @NotNull
    public ModifierEditor<T> addPublic() { return setModifier(getModifier() & Modifier.PUBLIC); }

    @NotNull
    public ModifierEditor<T> removePublic() { return setModifier(getModifier() & ~Modifier.PUBLIC); }

    @NotNull
    public ModifierEditor<T> addProtected() { return setModifier(getModifier() & Modifier.PROTECTED); }

    @NotNull
    public ModifierEditor<T> removeProtected() { return setModifier(getModifier() & ~Modifier.PROTECTED); }

    @NotNull
    public ModifierEditor<T> addPrivate() { return setModifier(getModifier() & Modifier.PRIVATE); }

    @NotNull
    public ModifierEditor<T> removePrivate() { return setModifier(getModifier() & ~Modifier.PRIVATE); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> addAbstract() { return setModifier(getModifier() & Modifier.ABSTRACT); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> removeAbstract() { return setModifier(getModifier() & ~Modifier.ABSTRACT); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> addNative() { return setModifier(getModifier() & Modifier.NATIVE); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> removeNative() { return setModifier(getModifier() & ~Modifier.NATIVE); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> addStatic() { return setModifier(getModifier() & Modifier.STATIC); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> removeStatic() { return setModifier(getModifier() & ~Modifier.STATIC); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> addInterface() { return setModifier(getModifier() & Modifier.INTERFACE); }

    /**
     * @deprecated Unsafe operation
     */
    @Deprecated
    @NotNull
    public ModifierEditor<T> removeInterface() { return setModifier(getModifier() & ~Modifier.INTERFACE); }
}
