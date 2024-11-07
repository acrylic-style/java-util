package xyz.acrylicstyle.util.maven;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.generator.gnupg.GnupgSignatureArtifactGeneratorFactory;
import org.eclipse.aether.generator.gnupg.loaders.GpgAgentPasswordLoader;
import org.eclipse.aether.generator.gnupg.loaders.GpgConfLoader;
import org.eclipse.aether.generator.gnupg.loaders.GpgEnvLoader;
import org.eclipse.aether.spi.artifact.generator.ArtifactGeneratorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.supplier.SessionBuilderSupplier;
import org.eclipse.aether.transport.apache.ApacheTransporterFactory;
import org.eclipse.aether.transport.jdk.JdkTransporterFactory;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RepositorySystemProvider {
    public static RepositorySystem provide() {
        return new RepositorySystemSupplier() {
            @Override
            protected Map<String, ArtifactGeneratorFactory> createArtifactGeneratorFactories() {
                var result = super.createArtifactGeneratorFactories();
                result.put(GnupgSignatureArtifactGeneratorFactory.NAME, new GnupgSignatureArtifactGeneratorFactory(
                        getArtifactPredicateFactory(), gnupgSignatureArtifactGeneratorFactoryLoaders()
                ));
                return result;
            }
            
            private Map<String, GnupgSignatureArtifactGeneratorFactory.Loader> gnupgSignatureArtifactGeneratorFactoryLoaders() {
                Map<String, GnupgSignatureArtifactGeneratorFactory.Loader> loaders = new LinkedHashMap<>();
                loaders.put(GpgEnvLoader.NAME, new GpgEnvLoader());
                loaders.put(GpgConfLoader.NAME, new GpgConfLoader());
                loaders.put(GpgAgentPasswordLoader.NAME, new GpgAgentPasswordLoader());
                return loaders;
            }

            @Override
            protected Map<String, TransporterFactory> createTransporterFactories() {
                Map<String, TransporterFactory> result = super.createTransporterFactories();
                result.put(JdkTransporterFactory.NAME, new JdkTransporterFactory(getChecksumExtractor(), getPathProcessor()));
                result.put(ApacheTransporterFactory.NAME, new ApacheTransporterFactory(getChecksumExtractor(), getPathProcessor()));
                return result;
            }
        }.get();
    }

    public static RepositorySystemSession provideSession(@NotNull RepositorySystem system, @NotNull Consumer<RepositorySystemSession.@NotNull SessionBuilder> configure) {
        RepositorySystemSession.SessionBuilder builder = new SessionBuilderSupplier(system).get();
        configure.accept(builder);
        return builder.build();
    }
}
