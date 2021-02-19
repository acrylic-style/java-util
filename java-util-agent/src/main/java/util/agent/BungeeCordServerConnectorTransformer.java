package util.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.jetbrains.annotations.NotNull;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class BungeeCordServerConnectorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(@NotNull ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equals("net/md_5/bungee/ServerConnector") && !className.equals("net/md_5/bungee/connection/InitialHandler")) return null;
        try {
            System.out.println("[BungeeCordServerConnectorTransformer] Transforming " + className);
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(className.replace("/", "."));
            CtMethod method = cc.getDeclaredMethod("handle", new CtClass[]{cp.get("net.md_5.bungee.protocol.PacketWrapper")});
            method.insertBefore(
                    "        if ($1.packet == null) {\n" +
                        "            this.ch.close();\n" +
                        "            " + (JavaAgent.verbose ? "" : "//") + "bungee.getLogger().info(\"Closed connection from \" + this.ch.getHandle());\n" +
                        "        }");
            byte[] buf = cc.toBytecode();
            cc.detach();
            System.out.println("[BungeeCordServerConnectorTransformer] Successfully transformed " + className);
            return buf;
        } catch (Throwable e) {
            System.out.println("[BungeeCordServerConnectorTransformer] encountered error - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
}
