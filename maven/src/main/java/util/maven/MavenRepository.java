package util.maven;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MavenRepository {
    private final List<Repository> repositories = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();
    private final List<Map.Entry<String, String>> exclude = new ArrayList<>();

    public void addRepository(@NotNull Repository repository) {
        repositories.add(repository);
    }

    public void addDependency(@NotNull Dependency dependency) {
        dependencies.add(dependency);
    }

    public void addExclude(@NotNull String groupId, @NotNull String artifactId) {
        exclude.add(new AbstractMap.SimpleImmutableEntry<>(groupId, artifactId));
    }

    @NotNull
    public List<Repository> getRepositories() {
        return repositories;
    }

    @NotNull
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @NotNull
    public List<Map.Entry<String, String>> getExclude() {
        return exclude;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public MavenRepositoryFetcher newFetcher(@NotNull File saveTo) {
        return new MavenRepositoryFetcher(this, saveTo);
    }
}
