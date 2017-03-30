/*
 * Copyright 2016 Palantir Technologies
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.atlasdb.testing;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.google.common.base.Throwables;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.configuration.ShutdownStrategy;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.logging.LogDirectory;

public class DockerProxyRuleTest {
    private static final DockerComposeRule DOCKER_COMPOSE_RULE = DockerComposeRule.builder()
            .file("docker/services.yml")
            .saveLogsTo(LogDirectory.circleAwareLogDirectory(DockerProxyRuleTest.class))
            .waitingForService("webserver", Container::areAllPortsOpen)
            .shutdownStrategy(ShutdownStrategy.AGGRESSIVE_WITH_NETWORK_CLEANUP)
            .build();

    private static final DockerProxyRule PROXY_RULE = new DockerProxyRule(
            DOCKER_COMPOSE_RULE.projectName(),
            DockerProxyRuleTest.class);

    @ClassRule
    public static final RuleChain RULE_CHAIN = RuleChain.outerRule(DOCKER_COMPOSE_RULE)
            .around(PROXY_RULE);

    @Test
    public void canReachDockerContainerByHostname() throws IOException, InterruptedException {
        URLConnection urlConnection = new URL("http://webserver").openConnection();
        urlConnection.connect();
    }

    @Test(expected = IllegalStateException.class)
    public void runningProxyRuleBeforeDockerComposeRuleFails() {
        try {
            new DockerProxyRule(ProjectName.fromString("doesnotexist"), DockerProxyRuleTest.class).before();
        } catch (Throwable e) {
            Throwables.propagate(e);
        }
    }
}
