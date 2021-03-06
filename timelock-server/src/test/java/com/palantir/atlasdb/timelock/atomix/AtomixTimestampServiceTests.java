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
package com.palantir.atlasdb.timelock.atomix;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.palantir.atlasdb.timestamp.AbstractTimestampServiceTests;
import com.palantir.timestamp.TimestampManagementService;
import com.palantir.timestamp.TimestampService;

import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.local.LocalServerRegistry;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.variables.DistributedLong;

public class AtomixTimestampServiceTests extends AbstractTimestampServiceTests {
    private static final Address LOCAL_ADDRESS = new Address("localhost", 8700);
    private static final String CLIENT_KEY = "client";
    private static final AtomixReplica ATOMIX_REPLICA = AtomixReplica.builder(LOCAL_ADDRESS)
            .withStorage(Storage.builder()
                    .withStorageLevel(StorageLevel.MEMORY)
                    .build())
            .withTransport(new LocalTransport(new LocalServerRegistry()))
            .build();

    private AtomixTimestampService atomixTimestampService;

    @BeforeClass
    public static void startAtomix() {
        ATOMIX_REPLICA.bootstrap().join();
    }

    @AfterClass
    public static void stopAtomix() {
        ATOMIX_REPLICA.leave();
    }

    @Override
    protected TimestampService getTimestampService() {
        return getSingletonTimestampManagementService();
    }

    @Override
    protected TimestampManagementService getTimestampManagementService() {
        return getSingletonTimestampManagementService();
    }

    private AtomixTimestampService getSingletonTimestampManagementService() {
        if (atomixTimestampService == null) {
            DistributedLong distributedLong = DistributedValues.getTimestampForClient(ATOMIX_REPLICA, CLIENT_KEY);
            atomixTimestampService = new AtomixTimestampService(distributedLong);
        }
        return atomixTimestampService;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfRequestingTooManyTimestamps() {
        getTimestampService().getFreshTimestamps(AtomixTimestampService.MAX_GRANT_SIZE + 1);
    }

    @Override
    public void canReturnManyUniqueTimestampsInParallel() {
    }
}
