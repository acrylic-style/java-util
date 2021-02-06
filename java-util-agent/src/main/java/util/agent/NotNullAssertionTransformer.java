package util.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.jetbrains.annotations.NotNull;
import util.base.Arrays;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class NotNullAssertionTransformer implements ClassFileTransformer {
    private ClassPool cp = null;
    private final Set<ClassLoader> registeredClassLoader = Collections.synchronizedSet(new HashSet<>());

    public NotNullAssertionTransformer() {
        System.out.println("[Transformer: NotNullAssertion] @NotNull assertion transformer was initialized");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (cp == null) cp = ClassPool.getDefault();
            if (!registeredClassLoader.contains(loader)) {
                cp.appendClassPath(new LoaderClassPath(loader));
                registeredClassLoader.add(loader);
            }
            CtClass cc = cp.get(className.replace("/", "."));
            boolean modified = false;
            for (CtMethod method : cc.getDeclaredMethods()) {
                try {
                    List<List<Annotation>> annotations = Arrays.toList(method.getAvailableParameterAnnotations());
                    Set<Integer> params = new HashSet<>();
                    Map<Integer, String> messages = new HashMap<>();
                    AtomicInteger curr = new AtomicInteger(1);
                    for (List<Annotation> annotationList : annotations) {
                        for (Annotation annotation : annotationList) {
                            if (annotation.annotationType().equals(NotNull.class) || annotation.annotationType().getCanonicalName().equals("javax.annotation.Nonnull")) {
                                params.add(curr.get());
                                if (annotation.annotationType().equals(NotNull.class)) {
                                    String msg = ((NotNull) annotation).value();
                                    messages.put(curr.get(), msg.isEmpty() ? String.format("Parameter #%d cannot be null", curr.get()) : msg);
                                } else {
                                    messages.put(curr.get(), String.format("Parameter #%d cannot be null", curr.get()));
                                }
                                break;
                            }
                        }
                        curr.incrementAndGet();
                    }
                    for (Integer param : params) {
                        if (JavaAgent.verbose) {
                            System.out.printf("[Transformer: NotNullAssertion] Adding @NotNull assertions to param #%d on method " + method.toString() + " on class " + className + "%n", param);
                        }
                        modified = true;
                        method.insertBefore(String.format("if ($%d == null) throw new NullPointerException(\"%s\");", param, messages.get(param)));
                    }
                } catch (RuntimeException e) {
                    if (JavaAgent.debug) e.printStackTrace();
                }
            }
            byte[] buf = cc.toBytecode();
            cc.detach();
            return modified ? buf : null;
        } catch (NotFoundException | CannotCompileException | IOException e) {
            return null; // ignore
        }
    }
}
