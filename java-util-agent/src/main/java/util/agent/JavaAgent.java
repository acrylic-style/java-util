package util.agent;

import org.jetbrains.annotations.NotNull;
import util.ReflectionHelper;
import util.agent.reflector.PrivateClass;
import util.option.OptionParser;
import util.option.OptionParserResult;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.instrument.Instrumentation;

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
        instrumentation.addTransformer(new ProxyGeneratorTransformer());
        System.out.println("[JavaAgent] Available options: verbose, debug, NoNotNullAssertionTransformer");
        OptionParser parser = new OptionParser();
        options = parser.parse(arg == null ? new String[0] : arg.split(","));
        debug = options.has("debug");
        if (debug) System.out.println("[JavaAgent] Debug logging is enabled");
        verbose = options.has("verbose");
        if (verbose) System.out.println("[JavaAgent] Verbose logging is enabled");
        try {
            PrivateClass clazz = PrivateClass.make(NotNull.class);
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
            System.err.println("Could not modify RetentionPolicy for " + NotNull.class.getCanonicalName());
            throwable.printStackTrace();
        }
        if (options.has("NotNullAssertionTransformer")) {
            instrumentation.addTransformer(new NotNullAssertionTransformer());
        }
        ReflectionHelper.getPackagesMethod.getExecutable().getAnnotations();
    }
}
