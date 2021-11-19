package util.maven.test;

import org.junit.jupiter.api.Test;
import util.maven.Repository;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {
    @Test
    public void resolve() {
        String url = Repository.mavenCentral().resolve("org.lwjgl:lwjgl-openal:3.2.2:jar:natives-windows");
        assertEquals(Repository.MAVEN_CENTRAL_URL + "org/lwjgl/lwjgl-openal/3.2.2/lwjgl-openal-3.2.2-natives-windows.jar", url);
        url = Repository.mavenCentral().resolve("org.lwjgl", "lwjgl-openal", "3.2.2");
        assertEquals(Repository.MAVEN_CENTRAL_URL + "org/lwjgl/lwjgl-openal/3.2.2/lwjgl-openal-3.2.2.jar", url);
    }
}
