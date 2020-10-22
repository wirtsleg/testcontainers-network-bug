/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package com.github.wirtzleg.networkbug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.github.dockerjava.api.command.CreateNetworkCmd;
import org.junit.rules.ExternalResource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.ResourceReaper;

/**
 * Docker network with user-defined name.
 */
public class NamedNetwork extends ExternalResource implements Network {
    /** Network name. */
    private final String name;

    /** Network ID. */
    private final String id;

    /**
     * @param name Network name.
     */
    public NamedNetwork(String name) {
        this.name = name;
        id = create();
    }

    /**
     * Create docker network.
     *
     * @return Network ID.
     */
    private String create() {
        CreateNetworkCmd createNetCmd = DockerClientFactory.instance().client().createNetworkCmd();

        createNetCmd.withName(name);
        createNetCmd.withCheckDuplicate(true);

        Map<String, String> labels = createNetCmd.getLabels();
        labels = new HashMap<>(labels != null ? labels : Collections.emptyMap());
        labels.putAll(DockerClientFactory.DEFAULT_LABELS);
        createNetCmd.withLabels(labels);

        return createNetCmd.exec().getId();
    }

    /** {@inheritDoc} */
    @Override public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override protected void after() {
        close();
    }

    /** {@inheritDoc} */
    @Override public void close() {
        ResourceReaper.instance().removeNetworkById(id);
    }
}
