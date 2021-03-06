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
package com.palantir.paxos;

import javax.annotation.Nullable;

import com.palantir.paxos.persistence.generated.remoting.PaxosAcceptorPersistence.PaxosPromiseProto;
import com.palantir.paxos.persistence.generated.remoting.PaxosAcceptorPersistence.PaxosPromiseProto.Builder;

public class PaxosPromises {
    private PaxosPromises() {
        // No instances.
    }

    public static PaxosPromiseProto toProto(PaxosPromise result) {
        Builder builder = PaxosPromiseProto.newBuilder().
                setAck(result.ack).
                setPromisedId(result.promisedId.persistToProto());
        if (result.lastAcceptedId != null) {
            builder.setLastAcceptedId(result.lastAcceptedId.persistToProto());
        }
        if (result.lastAcceptedValue != null) {
            builder.setLastAcceptedValue(result.lastAcceptedValue.persistToProto());
        }

        return builder.build();
    }

    public static PaxosPromise fromProto(PaxosPromiseProto proto) {
        boolean ack = proto.getAck();
        PaxosProposalId promisedId = PaxosProposalId.hydrateFromProto(proto.getPromisedId());
        if (!ack) {
            return PaxosPromise.reject(promisedId);
        } else {
            @Nullable PaxosProposalId lastAcceptedId = null;
            @Nullable PaxosValue lastAcceptedValue = null;
            if (proto.hasLastAcceptedId()) {
                lastAcceptedId = PaxosProposalId.hydrateFromProto(proto.getLastAcceptedId());
            }
            if (proto.hasLastAcceptedValue()) {
                lastAcceptedValue = PaxosValue.hydrateFromProto(proto.getLastAcceptedValue());
            }
            return PaxosPromise.accept(promisedId, lastAcceptedId, lastAcceptedValue);
        }
    }

}
