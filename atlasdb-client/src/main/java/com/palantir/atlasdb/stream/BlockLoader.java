/*
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
package com.palantir.atlasdb.stream;

import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.stream.LongStream;

public class BlockLoader implements BlockGetter {
    private final BiConsumer<Long, OutputStream> singleBlockLoader;
    private final int blockSizeInBytes;

    public BlockLoader(BiConsumer<Long, OutputStream> singleBlockLoader, int blockSizeInBytes) {
        this.singleBlockLoader = singleBlockLoader;
        this.blockSizeInBytes = blockSizeInBytes;
    }

    @Override
    public void get(long firstBlock, long numBlocks, OutputStream destination) {
        LongStream.range(firstBlock, firstBlock + numBlocks)
                .forEach(i -> singleBlockLoader.accept(i, destination));
    }

    @Override
    public int expectedBlockLength() {
        return blockSizeInBytes;
    }
}
