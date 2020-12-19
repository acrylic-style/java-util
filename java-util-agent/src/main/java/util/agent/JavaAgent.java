package util.agent;

import java.lang.instrument.Instrumentation;

public class JavaAgent {
    public static void agentmain(String arg, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ProxyGeneratorTransformer());
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ProxyGeneratorTransformer());
    }
}
