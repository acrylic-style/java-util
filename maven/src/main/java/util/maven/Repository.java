package util.maven;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Repository {
    public static final String MAVEN_LOCAL_NAME = "local";
    public static final String MAVEN_CENTRAL_NAME = "central";
    public static final String MAVEN_CENTRAL_URL = "https://repo.maven.apache.org/maven2/";
    private final String name;
    private final String url;
    private final boolean isFile;
    private final char separator;

    protected Repository(@NotNull String name, @NotNull String url, boolean isFile) {
        this.name = name;
        if (url.endsWith("/")) {
            this.url = url;
        } else {
            this.url = url + "/";
        }
        this.isFile = isFile;
        if (isFile) {
            separator = File.separatorChar;
        } else {
            separator = '/';
        }
    }

    @NotNull
    public static Repository mavenLocal() {
        return file(MAVEN_LOCAL_NAME, new File(System.getProperty("user.home", "."), ".m2/repository"));
    }

    @NotNull
    public static Repository mavenCentral() {
        return url(MAVEN_CENTRAL_NAME, MAVEN_CENTRAL_URL);
    }

    @NotNull
    public static Repository url(@NotNull String name, @NotNull String url) {
        Objects.requireNonNull(url, "URL cannot be null");
        return new Repository(name, url, false);
    }

    @NotNull
    public static Repository file(@NotNull String name, @NotNull File file) {
        Objects.requireNonNull(file, "file cannot be null");
        return new Repository(name, file.getAbsolutePath(), true);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public boolean isFile() {
        return isFile;
    }

    @NotNull
    public String resolve(@NotNull String dependencyNotation) {
        String[] split = dependencyNotation.split(":");
        if (split.length < 3) throw new IllegalArgumentException("groupId, artifactId, version are required (groupId:artifactId:version[[:type][:classifier]])");
        String groupId = split[0];
        String artifactId = split[1];
        String version = split[2];
        String type = "jar";
        if (split.length >= 4) type = split[3];
        String classifier = "";
        if (split.length >= 5) classifier = "-" + split[4];
        return resolve(groupId, artifactId, version, classifier, type);
    }

    @NotNull
    public String resolve(@NotNull Dependency dependency) {
        return resolve(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getClassifier(), dependency.getType());
    }

    @NotNull
    public String resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        return resolve(groupId, artifactId, version, "", "jar");
    }

    @NotNull
    public String resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String type) {
        return resolve(groupId, artifactId, version, "", type);
    }

    @NotNull
    public String resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String classifier, @NotNull String type) {
        return url + groupId.replace('.', separator) + separator + artifactId + separator + version + separator + artifactId + "-" + version + classifier + "." + type;
    }
}
