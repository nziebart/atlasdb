/*
 * Copyright 2015 Palantir Technologies
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
package com.palantir.lock;

import java.util.concurrent.locks.ReadWriteLock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * A descriptor for a {@link ReadWriteLock}, identified by a lock ID (a unique
 * string).
 *
 * @author jtamer
 */
public class AtlasCellLockDescriptor {

    private AtlasCellLockDescriptor() {
        // cannot instantiate
    }

    /** Returns a {@code LockDescriptor} instance for the given table, row, and column. */
    public static LockDescriptor of(String tableName, byte[] rowName, byte[] colName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(tableName));
        Preconditions.checkNotNull(rowName);
        Preconditions.checkNotNull(colName);
        byte[] tableBytes = tableName.getBytes();
        byte[] bytes = new byte[tableBytes.length + 1 + rowName.length + 1 + colName.length];
        System.arraycopy(tableBytes, 0, bytes, 0, tableBytes.length);
        System.arraycopy(rowName, 0, bytes, tableBytes.length + 1, rowName.length);
        System.arraycopy(colName, 0, bytes, tableBytes.length + 1 + rowName.length + 1, colName.length);
        return new LockDescriptor(bytes);
    }
}
