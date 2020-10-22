package com.github.wirtzleg.networkbug;

import java.time.Duration;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.Network;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExampleTest {
    static {
        org.testcontainers.Testcontainers.exposeHostPorts(8080);
    }

    @Test
    void shouldFail() {
        Network net = new NamedNetwork("z" + System.currentTimeMillis());

        KeycloakContainer keycloak = new KeycloakContainer()
            .withEnv("DB_VENDOR", "h2")
            .withNetwork(net)
            .withNetworkAliases("keycloak")
            .withStartupTimeout(Duration.ofMinutes(1));

        assertThrows(ContainerLaunchException.class, keycloak::start);
    }

    @Test
    void shouldNotFail() {
        Network net = new NamedNetwork("a" + System.currentTimeMillis());

        KeycloakContainer keycloak = new KeycloakContainer()
            .withEnv("DB_VENDOR", "h2")
            .withNetwork(net)
            .withNetworkAliases("keycloak")
            .withStartupTimeout(Duration.ofMinutes(1));

        keycloak.start();
    }
}
