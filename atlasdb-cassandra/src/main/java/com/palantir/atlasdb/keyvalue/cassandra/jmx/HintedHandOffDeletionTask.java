/*
 * Copyright 2015 Palantir Technologies
 * <p>
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://opensource.org/licenses/BSD-3-Clause
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.atlasdb.keyvalue.cassandra.jmx;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HintedHandOffDeletionTask implements Callable<Void> {
    private static final Logger log = LoggerFactory.getLogger(HintedHandOffDeletionTask.class);
    private final CassandraJmxCompactionClient client;

    HintedHandOffDeletionTask(CassandraJmxCompactionClient client) {
        this.client = client;
    }

    @Override
    public Void call() throws Exception {
        client.deleteLocalHints();
        log.info("Deleted local hints.");
        return null;
    }
}
