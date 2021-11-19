package util.maven;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import util.base.Bytes;
import util.base.Strings;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JarUtils {
    /**
     * Remap the class names.
     * @param src source jar (zip) file
     * @param fileSuffix suffix to apply to src file name.
     * @param prefix class name prefix without trailing dot or slash
     * @return destination file
     * @throws IOException if an I/O error occurs
     */
    @NotNull
    public static File remapJarWithClassPrefix(@NotNull File src, @NotNull String fileSuffix, @NotNull String prefix) throws IOException {
        return remapJarWithClassPrefix(src, fileSuffix, prefix, new PrefixClassRemapper(prefix));
    }

    /**
     * Remap the class names.
     * @param src source jar (zip) file
     * @param fileSuffix suffix to apply to src file name.
     * @param prefix class name prefix without trailing dot or slash
     * @return destination file
     * @throws IOException if an I/O error occurs
     */
    @NotNull
    public static File remapJarWithClassPrefix(@NotNull File src, @NotNull String fileSuffix, @NotNull String prefix, @NotNull Remapper remapper) throws IOException {
        String[] split = src.getName().split("\\.");
        StringBuilder name = new StringBuilder(split[0]);
        for (int i = 1; i < split.length - 1; i++) name.append('.').append(split[i]);
        String extension = "";
        if (split.length > 1) extension = "." + split[split.length - 1];
        File dst = new File(src.getParentFile(), name + fileSuffix + extension);
        remapJarWithClassPrefix(src, dst, prefix, remapper);
        return dst;
    }

    public static void remapJarWithClassPrefix(@NotNull File src, @NotNull File dst, @NotNull String prefix) throws IOException {
        remapJarWithClassPrefix(src, dst, prefix, new PrefixClassRemapper(prefix));
    }

    public static void remapJarWithClassPrefix(@NotNull File src, @NotNull File dst, @NotNull String prefix, @NotNull Remapper remapper) throws IOException {
        ZipFile zipFile = new ZipFile(src);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dst));
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                out.putNextEntry(new ZipEntry(prefix.replace('.', '/') + '/' + entry.getName()));
                byte[] bytes = remapClassWithClassPrefix(zipFile.getInputStream(entry), prefix, remapper);
                Bytes.copy(new ByteArrayInputStream(bytes), out);
            } else {
                out.putNextEntry(new ZipEntry(entry.getName()));
                if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                    StringBuilder sb = new StringBuilder();
                    for (String line : Strings.readLines(zipFile.getInputStream(entry))) {
                        if (line.startsWith("#")) {
                            sb.append(line);
                        } else {
                            String[] split = line.split(": ");
                            if (split.length == 1) {
                                sb.append(split[0]);
                            } else {
                                sb.append(split[0]).append(": ");
                                StringBuilder value = new StringBuilder();
                                for (int i = 1; i < split.length; i++) value.append(split[i]);
                                if (!split[0].equals("Import-Package") && line.matches("^\\D+\\..+")) {
                                    sb.append(remapper.map(value.toString()).replace('/', '.'));
                                } else {
                                    sb.append(value);
                                }
                            }
                        }
                        sb.append("\n");
                    }
                    Strings.writeString(out, sb.toString());
                } else if (entry.getName().startsWith("META-INF/services/")) {
                    StringBuilder sb = new StringBuilder();
                    for (String line : Strings.readLines(zipFile.getInputStream(entry))) {
                        if (line.startsWith("#")) {
                            sb.append(line);
                        } else if (line.matches("^\\D+\\..+")) { // if not blank:
                            sb.append(remapper.map(line).replace('/', '.'));
                        }
                        sb.append("\n");
                    }
                    Strings.writeString(out, sb.toString());
                    out.closeEntry();
                    String service = entry.getName().replaceFirst("META-INF/services/(.*)", "$1");
                    out.putNextEntry(new ZipEntry("META-INF/services/" + remapper.map(service).replace('/', '.')));
                    Strings.writeString(out, sb.toString());
                } else {
                    Bytes.copy(zipFile.getInputStream(entry), out);
                }
            }
            out.closeEntry();
        }
        zipFile.close();
        out.close();
    }

    public static byte[] remapClassWithClassPrefix(@NotNull InputStream in, @NotNull String prefix, @NotNull Remapper remapper) throws IOException {
        ClassReader cr = new ClassReader(in);
        ClassWriter cw = new ClassWriter(0);
        cr.accept(new ClassRemapper(cw, remapper), 0);
        return cw.toByteArray();
    }
}
