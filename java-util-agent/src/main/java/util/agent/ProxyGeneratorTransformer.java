package util.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ProxyGeneratorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(@NotNull ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equals("sun/misc/ProxyGenerator")) return null;
        try {
            System.out.println("[Transformer: ProxyGenerator] Transforming " + className);
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get("sun.misc.ProxyGenerator");
            CtMethod method = cc.getDeclaredMethod("checkReturnTypes", new CtClass[]{cp.get("java.util.List")});
            method.addLocalVariable("m", cp.get("sun.misc.ProxyGenerator$ProxyMethod"));
            method.insertBefore(
                    "        /*\n" +
                    "         * If there is only one method with a given signature, there\n" +
                    "         * cannot be a conflict.  This is the only case in which a\n" +
                    "         * primitive (or void) return type is allowed.\n" +
                    "         */\n" +
                    "        if ($1.size() < 2) {\n" +
                    "            return;\n" +
                    "        }\n" +
                    "        // Always don't return anything.\n" +
                    "        if (true) {\n" +
                    "            m = $1.get(0);\n" +
                    "            $1.clear();\n" +
                    "            $1.add(m);\n" +
                    "            return;\n" +
                    "        }");
            byte[] buf = cc.toBytecode();
            cc.detach();
            System.out.println("[Transformer: ProxyGenerator] Successfully transformed " + className);
            return buf;
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
