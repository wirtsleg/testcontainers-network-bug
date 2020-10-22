package com.github.wirtzleg.networkbug;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExampleTest {
    static {
        Testcontainers.exposeHostPorts(8080);
    }

    @Test
    void shouldFail() {
        Network net = new NamedNetwork("z" + System.currentTimeMillis());

        GenericContainer<?> keycloak = createKeycloakContainer(net);

        assertThrows(ContainerLaunchException.class, keycloak::start);
    }

    @Test
    void shouldNotFail() {
        Network net = new NamedNetwork("a" + System.currentTimeMillis());

        GenericContainer<?> keycloak = createKeycloakContainer(net);

        keycloak.start();
    }

    private GenericContainer<?> createKeycloakContainer(Network net) {
        GenericContainer<?> keycloak = new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:11.0.2"))
            .withEnv("DB_VENDOR", "h2")
            .withNetwork(net)
            .withNetworkAliases("keycloak")
            .withExposedPorts(8080, 8443)
            .withCommand(
                "-c standalone.xml",
                "-Dkeycloak.profile.feature.upload_scripts=enabled"
            )
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin");

        keycloak.setWaitStrategy(Wait
            .forHttp("/auth")
            .forPort(8080)
            .withStartupTimeout(Duration.ofMinutes(1))
        );

        return keycloak;
    }
}
