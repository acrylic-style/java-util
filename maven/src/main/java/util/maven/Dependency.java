package util.maven;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Dependency {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String classifier;
    private final String type;
    @Nullable
    private String sha512 = null;
    @Nullable
    private String pomSha512 = null;

    private Dependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String classifier, @NotNull String type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.type = type;
    }

    @NotNull
    public Dependency withSha512(@Nullable String sha512) {
        this.sha512 = sha512;
        return this;
    }

    @NotNull
    public Dependency withPomSha512(@Nullable String sha512) {
        this.pomSha512 = sha512;
        return this;
    }

    @Nullable
    public String getSha512() {
        return sha512;
    }

    @Nullable
    public String getPomSha512() {
        return pomSha512;
    }

    @NotNull
    public String getGroupId() {
        return groupId;
    }

    @NotNull
    public String getArtifactId() {
        return artifactId;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    @NotNull
    public String getClassifier() {
        return classifier;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public Dependency jar() {
        return new Dependency(groupId, artifactId, version, classifier, "jar");
    }

    @NotNull
    public Dependency pom() {
        return new Dependency(groupId, artifactId, version, classifier, "pom").withSha512(pomSha512).withPomSha512(pomSha512);
    }

    @NotNull
    public Dependency copyWithoutHash() {
        return new Dependency(groupId, artifactId, version, classifier, type);
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", classifier='" + classifier + '\'' +
                ", type='" + type + '\'' +
                ", sha512='" + sha512 + '\'' +
                '}';
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return groupId.equals(that.groupId)
                && artifactId.equals(that.artifactId)
                && version.equals(that.version)
                && classifier.equals(that.classifier)
                && type.equals(that.type)
                && Objects.equals(sha512, that.sha512);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version, classifier, type, sha512);
    }

    @NotNull
    public static Dependency resolve(@NotNull String dependencyNotation) {
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

    @Contract(value = "_, _, _ -> new", pure = true)
    @NotNull
    public static Dependency resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        return new Dependency(groupId, artifactId, version, "", "jar");
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    @NotNull
    public static Dependency resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String type) {
        return new Dependency(groupId, artifactId, version, "", type);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    @NotNull
    public static Dependency resolve(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String classifier, @NotNull String type) {
        return new Dependency(groupId, artifactId, version, classifier, type);
    }
}
