package xyz.acrylicstyle.util.maven.test;

import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.maven.MavenResolver;

public class MavenResolverTest {
    @Test
    public void test() throws DependencyCollectionException, DependencyResolutionException {
        new MavenResolver(".libraries")
                .addMavenCentral()
                .addDependency("net.blueberrymc:native-util:2.1.2")
                .resolve()
                .forEach(System.out::println);
    }
}
