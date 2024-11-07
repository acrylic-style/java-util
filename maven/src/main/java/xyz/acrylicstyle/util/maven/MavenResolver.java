package xyz.acrylicstyle.util.maven;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MavenResolver {
    private final List<RemoteRepository> repositories = new ArrayList<>();
    private final List<Artifact> dependencies = new ArrayList<>();
    private final String destinationDirectory;

    public MavenResolver(@NotNull String destinationDirectory) {
        this.destinationDirectory = Objects.requireNonNull(destinationDirectory, "destinationDirectory");
    }

    @Contract("_ -> this")
    public @NotNull MavenResolver addRepository(@NotNull RemoteRepository repository) {
        repositories.add(Objects.requireNonNull(repository, "repository"));
        return this;
    }

    @Contract("_, _, _ -> this")
    public @NotNull MavenResolver addRepository(@Nullable String id, @Nullable String type, @NotNull String url) {
        return addRepository(new RemoteRepository.Builder(id, type, url).build());
    }

    @Contract("_ -> this")
    public @NotNull MavenResolver addRepository(@NotNull String url) {
        return addRepository(null, null, url);
    }

    @Contract("-> this")
    public @NotNull MavenResolver addMavenCentral() {
        return addRepository("central", "default", "https://repo.maven.apache.org/maven2/");
    }

    public @NotNull List<@NotNull RemoteRepository> getRepositories() {
        return repositories;
    }

    @Contract("_ -> this")
    public @NotNull MavenResolver addDependency(@NotNull Artifact dependency) {
        dependencies.add(Objects.requireNonNull(dependency, "dependency"));
        return this;
    }

    @Contract("_, _, _, _, _ -> this")
    public @NotNull MavenResolver addDependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String type, @NotNull String classifier) {
        return addDependency(new DefaultArtifact(groupId, artifactId, classifier, type, version));
    }

    @Contract("_, _, _ -> this")
    public @NotNull MavenResolver addDependency(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        return addDependency(groupId, artifactId, version, "jar", "");
    }

    @Contract("_ -> this")
    public @NotNull MavenResolver addDependency(@NotNull String dependency) {
        return addDependency(new DefaultArtifact(dependency));
    }

    @Contract(pure = true)
    public @NotNull List<@NotNull Artifact> getDependencies() {
        return dependencies;
    }

    @Contract("_ -> this")
    public @NotNull MavenResolver configure(@NotNull Consumer<MavenResolver> configure) {
        configure.accept(this);
        return this;
    }

    public @NotNull List<DependencyResult> resolve() throws DependencyCollectionException, DependencyResolutionException {
        return resolve((dependencyNode, list) -> true);
    }

    public @NotNull List<DependencyResult> resolve(@NotNull DependencyFilter dependencyFilter) throws DependencyCollectionException, DependencyResolutionException {
        List<DependencyResult> list = new ArrayList<>();
        try (RepositorySystem system = RepositorySystemProvider.provide()) {
            RepositorySystemSession session = RepositorySystemProvider.provideSession(system, this::configureRepositorySystemSessionBuilder);
            try {
                for (Artifact dependency : dependencies) {
                    CollectRequest collectRequest = new CollectRequest();
                    collectRequest.setRoot(new Dependency(dependency, "compile+runtime"));
                    collectRequest.setRepositories(repositories);

                    CollectResult collectResult = system.collectDependencies(session, collectRequest);
                    DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, dependencyFilter);
                    dependencyRequest.setRoot(collectResult.getRoot());
                    list.add(system.resolveDependencies(session, dependencyRequest));
                }
            } finally {
                if (session instanceof RepositorySystemSession.CloseableSession closeableSession) {
                    closeableSession.close();
                }
            }
        }
        return list;
    }

    protected void configureRepositorySystemSessionBuilder(RepositorySystemSession.SessionBuilder builder) {
        builder.withLocalRepositories(new LocalRepository(destinationDirectory));
    }
}
