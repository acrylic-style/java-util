package util.inject;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.MethodInfo;
import lombok.SneakyThrows;
import org.apache.openjpa.enhance.InstrumentationFactory;
import org.apache.openjpa.lib.log.NoneLogFactory;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class InterfaceAdapter implements ClassFileTransformer {
    private static boolean init = false;

    static void init() {
        if (init) return;
        Instrumentation instrumentation = InstrumentationFactory.getInstrumentation(new NoneLogFactory().getLog("InterfaceAdapter"));
        instrumentation.addTransformer(new InterfaceAdapter());
        init = true;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.startsWith("java.")) return null;
        if (className.startsWith("javassist.")) return null;
        if (filter(Injector.data, data -> className.matches(data.getBaseClass())).size() != 0) {
            return transformClass(className, classfileBuffer);
        }
        return null;
    }

    public static <E> List<E> filter(List<E> list, Function<E, Boolean> function) {
        List<E> newList = new ArrayList<>();
        list.forEach(v -> {
            if (function.apply(v)) newList.add(v);
        });
        return newList;
    }

    public static CtField findFieldByName(String name, CtField[] fields) {
        for (CtField field : fields) {
            if (field.getName().equals(name)) return field;
        }
        return null;
    }

    public byte[] transformClass(String className, byte[] bytecode) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(new ByteArrayInputStream(bytecode));
            InjectorData data = Objects.requireNonNull(filter(Injector.data, dataaa -> className.matches(dataaa.getBaseClass())).get(0));
            String tn = data.getInterfaceClass().getTypeName();
            CtClass clazzz = getCtClass(tn);
            if (data.getInterfaceClass().isInterface()) {
                clazz.addInterface(clazzz);
            } else {
                clazz.setSuperclass(clazzz);
            }
            for (CtConstructor constructor : clazz.getDeclaredConstructors()) {
                constructor.insertAfter("util.inject.Injector.updateAllFieldsTimer(" + data.getInterfaceClass().getCanonicalName() + ".class, this);");
            }
            List<CtMethod> methods = Arrays.asList(clazzz.getMethods());
            methods.sort(Comparator.comparingInt(InterfaceAdapter::getPriority));
            methods.forEach(method -> {
                try {
                    MethodInfo info = method.getMethodInfo();
                    AtomicBoolean modify = new AtomicBoolean(false);
                    AtomicReference<CtClass> target = new AtomicReference<>(method.getReturnType());
                    Injector.data.forEach(inj -> {
                        if (inj.getBaseClass().matches(className)) return;
                        if (modify.get()) return;
                        if (info.getDescriptor().contains(inj.getBaseClass())) {
                            modify.set(true);
                            target.set(getCtClass(inj.getInterfaceClass().getTypeName().replaceAll("\\.", "/")));
                        }
                    });
                    boolean isVoid = method.getReturnType().equals(CtClass.voidType);
                    String src = "{ " + (isVoid ? "" : "return ") + method.getName().replaceFirst("_", "") + "($$); }";
                    CtMethod impl = CtNewMethod.make(
                            target.get(),
                            "_" + method.getName().replaceFirst("_", ""),
                            convertClasses(method.getParameterTypes()),
                            convertClasses(method.getExceptionTypes()),
                            src,
                            clazz
                    );
                    clazz.addMethod(impl);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            return clazz.toBytecode();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to instrument class: " + className, ex);
        }
    }

    private CtClass[] convertClasses(CtClass[] classes) {
        if (classes.length == 0) return new CtClass[0];
        CtClass[] c = new CtClass[classes.length];
        try {
            for (int i = 0; i < classes.length; i++) {
                AtomicReference<CtClass> cl = new AtomicReference<>(classes[i]);
                AtomicBoolean changed = new AtomicBoolean(false);
                int finalI = i;
                Injector.data.forEach(inj -> {
                    if (inj.getBaseClass().matches(classes[finalI].getName())) return;
                    if (changed.get()) return;
                    if (classes[finalI].getName().contains(inj.getBaseClass())) {
                        cl.set(getCtClass(inj.getInterfaceClass().getTypeName().replaceAll("\\.", "/")));
                        changed.set(true);
                    }
                });
                c[i] = cl.get();
                if (i == classes.length - 1) return c;
            }
        } catch (Throwable throwable) { return c; }
        return c;
    }

    @SneakyThrows
    public static int getPriority(CtMethod method) {
        if (!method.hasAnnotation(LoadOrder.class)) {
            return LoadOrder.LoadPriority.NORMAL.getSlot();
        }
        return ((LoadOrder) method.getAnnotation(LoadOrder.class)).value().getSlot();
    }

    public static CtClass getCtClass(String clazz) {
        try {
            Constructor<?> cconstructor = Class.forName("javassist.CtClassType").getDeclaredConstructor(String.class, ClassPool.class);
            cconstructor.setAccessible(true);
            return (CtClass) cconstructor.newInstance(clazz, ClassPool.getDefault());
        } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not create CtClass", e);
        }
    }
}
