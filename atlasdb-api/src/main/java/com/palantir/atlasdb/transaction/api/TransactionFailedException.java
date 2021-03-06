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
package com.palantir.atlasdb.transaction.api;

/**
 * This is a generic exception for all failures thrown from a Transaction
 * <p>
 * Check {@link #canTransactionBeRetried()} to see if this Exception can be retried.
 * <p>
 * This method has no public constructors so {@link TransactionCommitFailedException}
 * is the only subclass that cannot be retried.
 *
 * @author carrino
 *
 */
public abstract class TransactionFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    TransactionFailedException(String message) {
        super(message);
    }

    public abstract boolean canTransactionBeRetried();

}
