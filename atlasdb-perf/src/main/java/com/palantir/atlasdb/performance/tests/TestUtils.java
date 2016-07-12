/**
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
package com.palantir.atlasdb.performance.tests;

import com.palantir.atlasdb.keyvalue.api.KeyValueService;
import com.palantir.atlasdb.keyvalue.api.TableReference;
import com.palantir.atlasdb.protos.generated.TableMetadataPersistence;
import com.palantir.atlasdb.table.description.TableDefinition;
import com.palantir.atlasdb.table.description.ValueType;
import com.palantir.atlasdb.transaction.api.ConflictHandler;

/**
 * Static utilities class for common performance test procedures.
 */
public class TestUtils {

    /**
     * Creates the table and returns a reference to it.
     * @param kvs the key-value service where the table is being created.
     * @param tableName the name of the table being created.
     * @param rowComponent the name of the row being created.
     * @param columnName the name of the column being created.
     * @return a reference to the newly created table.
     */
    public static TableReference createTable(KeyValueService kvs, String tableName, String rowComponent, String columnName) {
        TableReference tableRef = TableReference.createFromFullyQualifiedName(tableName);
        TableDefinition tableDef = new TableDefinition() {{
            rowName();
            rowComponent(rowComponent, ValueType.STRING);
            columns();
            column(columnName, columnName, ValueType.BLOB);
            conflictHandler(ConflictHandler.IGNORE_ALL);
            sweepStrategy(TableMetadataPersistence.SweepStrategy.NOTHING);
        }};
        kvs.createTable(tableRef, tableDef.toTableMetadata().persistToBytes());
        return tableRef;
    }
}