/**
 * Copyright 2017 Palantir Technologies
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
package com.palantir.atlasdb.keyvalue.cassandra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.palantir.atlasdb.AtlasDbConstants;
import com.palantir.atlasdb.cassandra.CassandraKeyValueServiceConfigManager;
import com.palantir.atlasdb.containers.CassandraContainer;
import com.palantir.atlasdb.containers.Containers;
import com.palantir.atlasdb.encoding.PtBytes;
import com.palantir.atlasdb.keyvalue.api.Cell;
import com.palantir.atlasdb.timestamp.AbstractDbTimestampBoundStoreTest;
import com.palantir.timestamp.MultipleRunningTimestampServiceError;
import com.palantir.timestamp.TimestampBoundStore;

public class CassandraTimestampBoundStoreTest extends AbstractDbTimestampBoundStoreTest {
    private static final long CASSANDRA_TIMESTAMP = 0L;
    private static final String ROW_AND_COLUMN_NAME = "ts";
    public static final long NEW_LIMIT = 100;

    @ClassRule
    public static final Containers CONTAINERS = new Containers(CassandraTimestampIntegrationTest.class)
            .with(new CassandraContainer());

    private CassandraKeyValueService kv = CassandraKeyValueService.create(
            CassandraKeyValueServiceConfigManager.createSimpleManager(CassandraContainer.KVS_CONFIG),
            CassandraContainer.LEADER_CONFIG);

    @Override
    public TimestampBoundStore createTimestampBoundStore() {
        return CassandraTimestampBoundStore.create(kv);
    }

    @Test
    public void storeWithEmptyTableThrows(){
        store.storeUpperLimit(NEW_LIMIT);
    }

    @Test
    public void canGetNewFormat(){
        insertTimestampWithCorrectId(NEW_LIMIT + 1);
        assertCorrect();
    }

    @Test
    public void canGetOldFormat(){
        insertTimestampOld(NEW_LIMIT + 1);
        assertCorrect();
    }

    @Test
    public void storeWithWrongTimestampCorrectIdSucceeds() {
        long limit = store.getUpperLimit();
        insertTimestampWithCorrectId(NEW_LIMIT);
        assertThat(limit).isNotEqualTo(NEW_LIMIT);
        store.storeUpperLimit(NEW_LIMIT + 1);
        assertCorrect();
    }

    @Test
    public void storeWithWrongTimestampNoIdSucceeds() {
        long limit = store.getUpperLimit();
        insertTimestampOld(NEW_LIMIT);
        assertThat(limit).isNotEqualTo(NEW_LIMIT);
        store.storeUpperLimit(NEW_LIMIT + 1);
        assertCorrect();
    }

    @Test
    public void storeWithWrongTimestampWrongIdThrows() {
        long limit = store.getUpperLimit();
        insertTimestampWithFakeId(NEW_LIMIT);
        assertThat(limit).isNotEqualTo(NEW_LIMIT);
        assertThatThrownBy(() -> store.storeUpperLimit(NEW_LIMIT + 1)).
                isExactlyInstanceOf(MultipleRunningTimestampServiceError.class);
    }

    @Test
    public void storeWithRightTimestampWrongIdThrows() {
        long limit = store.getUpperLimit();
        insertTimestampWithFakeId(limit);
        assertThatThrownBy(() -> store.storeUpperLimit(NEW_LIMIT + 1)).
                isExactlyInstanceOf(MultipleRunningTimestampServiceError.class);
    }

    @After
    public void cleanUp() {
        kv.dropTable(AtlasDbConstants.TIMESTAMP_TABLE);
    }

    private void assertCorrect() {
        assertThat(store.getUpperLimit()).isEqualTo(NEW_LIMIT + 1);
    }

    private void insertTimestampWithCorrectId(long value) {
        insertTimestampWithIdChanged(value, false);
    }

    private void insertTimestampWithFakeId(long value) {
        insertTimestampWithIdChanged(value, true);
    }

    private void insertTimestampOld(long value) {
        insertIntoTimestampTable(PtBytes.toBytes(value));
    }

    private void insertTimestampWithIdChanged(long value, boolean changeId) {
        long id = ((CassandraTimestampBoundStore) store).getId();
        if (changeId) {
            id = id % 2 + 1;
        }
        insertIntoTimestampTable(PtBytes.toBytes(Long.toString(id) + "_" + Long.toString(value)));
    }

    private void insertIntoTimestampTable(byte[] data) {
        kv.truncateTable(AtlasDbConstants.TIMESTAMP_TABLE);
        kv.put(AtlasDbConstants.TIMESTAMP_TABLE, ImmutableMap.of(Cell.create(PtBytes.toBytes(ROW_AND_COLUMN_NAME),
                PtBytes.toBytes(ROW_AND_COLUMN_NAME)), data), CASSANDRA_TIMESTAMP);
    }
}
