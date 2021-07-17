package util.agent;

import org.jetbrains.annotations.NotNull;
import util.agent.reflector.PrivateClass;
import util.option.OptionParser;
import util.option.OptionParserResult;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public class JavaAgent {
    public static OptionParserResult options;
    public static boolean debug = false;
    public static boolean verbose = false;

    public static void agentmain(String arg, Instrumentation instrumentation) {
        main(arg, instrumentation);
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        main(arg, instrumentation);
    }

    public static void main(String arg, Instrumentation instrumentation) {
        registerJavaAgents(instrumentation);
        instrumentation.addTransformer(new ProxyGeneratorTransformer());
        System.out.println("[JavaAgent] Available options: verbose, debug, NotNullAssertionTransformer, BungeeCordServerConnectorTransformer");
        OptionParser parser = new OptionParser();
        options = parser.parse(arg == null ? new String[0] : arg.split(","));
        debug = options.has("debug");
        if (debug) System.out.println("[JavaAgent] Debug logging is enabled");
        verbose = options.has("verbose");
        if (verbose) System.out.println("[JavaAgent] Verbose logging is enabled");
        if (options.has("NotNullAssertionTransformer")) {
            // it is known to break ReflectionHelper#findAllAnnotatedClasses, so i don't recommend using this
            // also breaks on many jvm versions since we can't access Class#annotationData
            hackRetentionPolicy(NotNull.class);
            instrumentation.addTransformer(new NotNullAssertionTransformer());
        }
        if (options.has("BungeeCordServerConnectorTransformer")) {
            instrumentation.addTransformer(new BungeeCordServerConnectorTransformer());
        }
    }

    private static void hackRetentionPolicy(Class<? extends Annotation> annotationClass) {
        try {
            PrivateClass clazz = PrivateClass.make(annotationClass);
            PrivateClass.AnnotationData annotationData = clazz.getAnnotationData();
            annotationData.getDeclaredAnnotations().put(Retention.class, new Retention() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Retention.class;
                }

                @Override
                public RetentionPolicy value() {
                    return RetentionPolicy.RUNTIME;
                }
            });
        } catch (Throwable throwable) {
            System.err.println("Could not modify RetentionPolicy for " + annotationClass.getCanonicalName());
            throwable.printStackTrace();
        }
    }

    private static void registerJavaAgents(final Instrumentation instrumentation) {
        JavaAgents.setInstance(new JavaAgents() {
            @Override
            public void addTransformer(ClassFileTransformer transformer) {
                instrumentation.addTransformer(transformer);
            }

            @Override
            public boolean removeTransformer(ClassFileTransformer transformer) {
                return instrumentation.removeTransformer(transformer);
            }

            @Override
            public boolean isRetransformClassesSupported() {
                return instrumentation.isRetransformClassesSupported();
            }

            @Override
            public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
                instrumentation.retransformClasses(classes);
            }

            @Override
            public boolean isRedefineClassesSupported() {
                return instrumentation.isRedefineClassesSupported();
            }

            @Override
            public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
                instrumentation.redefineClasses(definitions);
            }

            @Override
            public boolean isModifiableClass(Class<?> theClass) {
                return instrumentation.isModifiableClass(theClass);
            }

            @Override
            public Class<?>[] getAllLoadedClasses() {
                return instrumentation.getAllLoadedClasses();
            }

            @Override
            public Class<?>[] getInitiatedClasses(ClassLoader loader) {
                return instrumentation.getInitiatedClasses(loader);
            }

            @Override
            public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
                instrumentation.addTransformer(transformer, canRetransform);
            }

            @Override
            public long getObjectSize(Object object) {
                return instrumentation.getObjectSize(object);
            }

            @Override
            public void appendToSystemClassLoaderSearch(JarFile jarFile) {
                instrumentation.appendToSystemClassLoaderSearch(jarFile);
            }

            @Override
            public boolean isNativeMethodPrefixSupported() {
                return instrumentation.isNativeMethodPrefixSupported();
            }

            @Override
            public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
                instrumentation.setNativeMethodPrefix(transformer, prefix);
            }

            @Override
            public void appendToBootstrapClassLoaderSearch(JarFile jarFile) {
                instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
            }
        });
    }
}
