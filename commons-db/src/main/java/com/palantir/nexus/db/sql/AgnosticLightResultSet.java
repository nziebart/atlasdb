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
package com.palantir.nexus.db.sql;

import java.io.Closeable;
import java.util.Iterator;

import com.palantir.common.visitor.Visitor;

/**
 * This result set only loads one row at a time, and thus provides a
 * low-overhead solution for large queries.  Read the comments about the iterator before
 * using it, because there are non-obvious pitfalls.
 * @author dcohen
 *
 */
public interface AgnosticLightResultSet extends Iterable<AgnosticLightResultRow>, Closeable {

    @Override
    void close();

    void visitAndClose(Visitor<? super AgnosticLightResultRow> visitor);

    @Override
    Iterator<AgnosticLightResultRow> iterator();

    void setFetchSize(int fetchSize);
}
