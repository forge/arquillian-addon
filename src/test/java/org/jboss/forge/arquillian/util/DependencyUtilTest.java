/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.arquillian.util;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.arquillian.container.model.Container;
import org.jboss.forge.arquillian.util.DependencyUtil;
import org.jboss.forge.arquillian.container.model.Identifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DependencyUtilTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class DependencyUtilTest {

    @Test
    public void should_get_last_non_snapshot_version() {
        List<Coordinate> deps = new ArrayList<>();
        deps.add(DependencyBuilder.create().setVersion("1.0").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("1.0-SNAPSHOT").getCoordinate());

        String dep = DependencyUtil.getLatestNonSnapshotVersion(DependencyUtil.toVersionString(deps));

        assertThat(dep).isEqualTo("1.0");
    }

    @Test
    public void should_get_versions_only_supported_by_chameleon() throws Exception {
        List<Coordinate> deps = new ArrayList<>();
        deps.add(DependencyBuilder.create().setVersion("4.1").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("2.1").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("7.1").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("1.0-SNAPSHOT").getCoordinate());

        List<String> dep = DependencyUtil.toVersionString(deps,
            createContainer(Identifier.TOMCAT.getArtifactID(), Identifier.TOMCAT.getName()));

        assertThat(dep).doesNotContain("2.1", "4.1");
        assertThat(dep).contains("7.1");
    }

    @Test
    public void should_get_all_available_versions_if_not_supported_by_chameleon() throws Exception {
        List<Coordinate> deps = new ArrayList<>();
        deps.add(DependencyBuilder.create().setVersion("4.1").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("7.1").getCoordinate());

        List<String> dep = DependencyUtil.toVersionString(deps,
            createContainer("arquillian-jbossas-managed-4.2", "JBoss As"));

        assertThat(dep).contains("4.1", "7.1");
    }

    @Test
    public void should_return_latest_if_all_snapshots() {
        List<Coordinate> deps = new ArrayList<>();
        deps.add(DependencyBuilder.create().setVersion("1.0-SNAPSHOT").getCoordinate());
        deps.add(DependencyBuilder.create().setVersion("2.0-SNAPSHOT").getCoordinate());

        String dep = DependencyUtil.getLatestNonSnapshotVersion(DependencyUtil.toVersionString(deps));

        assertThat(dep).isEqualTo("2.0-SNAPSHOT");
    }

    @Test
    public void should_return_null_if_empty() {
        String dep = DependencyUtil.getLatestNonSnapshotVersion(Collections.emptyList());

        assertThat(dep).isNull();
    }

    private Container createContainer(String artifactId, String name) {
        Container container = new Container();
        container.setArtifactId(artifactId);
        container.setName(name);

        return container;
    }
}
