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
package com.palantir.atlasdb.sweep;

import java.util.Set;

import org.immutables.value.Value;

import com.palantir.atlasdb.keyvalue.api.Cell;

@Value.Immutable
public abstract class CellAndTimestamps {
    public abstract Cell cell();
    public abstract Set<Long> timestamps();

    public static CellAndTimestamps of(Cell cell, Set<Long> timestamps) {
        return ImmutableCellAndTimestamps.builder().cell(cell).timestamps(timestamps).build();
    }
}
