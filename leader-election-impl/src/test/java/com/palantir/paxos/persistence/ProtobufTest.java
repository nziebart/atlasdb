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
package com.palantir.paxos.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.palantir.paxos.PaxosPromise;
import com.palantir.paxos.PaxosPromises;
import com.palantir.paxos.PaxosProposal;
import com.palantir.paxos.PaxosProposalId;
import com.palantir.paxos.PaxosResponse;
import com.palantir.paxos.PaxosResponseImpl;
import com.palantir.paxos.PaxosResponses;
import com.palantir.paxos.PaxosValue;
import com.palantir.paxos.persistence.generated.PaxosPersistence;
import com.palantir.paxos.persistence.generated.remoting.PaxosAcceptorPersistence;
import com.palantir.paxos.persistence.generated.remoting.PaxosAcceptorPersistence.PaxosPromiseProto;

public class ProtobufTest {
    @Test
    public void testPaxosValuePersistence() throws Exception {
        PaxosValue expected;
        byte[] persisted;
        PaxosValue actual;

        expected = new PaxosValue("leader1", 2, new byte[]{8, 0, 1, 2, 5});
        persisted = expected.persistToBytes();
        actual = PaxosValue.hydrateFromProto(PaxosPersistence.PaxosValue.parseFrom(persisted));
        assertEquals(expected, actual);

        expected = new PaxosValue("dealer2", 8, null);
        persisted = expected.persistToBytes();
        actual = PaxosValue.hydrateFromProto(PaxosPersistence.PaxosValue.parseFrom(persisted));
        assertEquals(expected, actual);
    }

    @Test
    public void testPaxosProposalIdPersistence() throws Exception {
        PaxosProposalId expected;
        PaxosPersistence.PaxosProposalId persisted;
        PaxosProposalId actual;

        expected = new PaxosProposalId(10, "string");
        persisted = expected.persistToProto();
        actual = PaxosProposalId.hydrateFromProto(persisted);
        assertEquals(expected, actual);

        expected = new PaxosProposalId(10, "");
        persisted = expected.persistToProto();
        actual = PaxosProposalId.hydrateFromProto(persisted);
        assertEquals(expected, actual);
    }

    @Test
    public void testPaxosProposalPersistence() throws Exception {
        PaxosProposal expected;
        PaxosAcceptorPersistence.PaxosProposal persisted;
        PaxosProposal actual;

        expected = new PaxosProposal(new PaxosProposalId(55, "nonce"), new PaxosValue("red leader", 93, null));
        persisted = expected.persistToProto();
        actual = PaxosProposal.hydrateFromProto(persisted);
        assertEquals(expected, actual);

        expected = new PaxosProposal(new PaxosProposalId(0, "noice"), new PaxosValue("", 93, new byte[]{}));
        persisted = expected.persistToProto();
        actual = PaxosProposal.hydrateFromProto(persisted);
        assertEquals(expected, actual);
    }

    @Test
    public void testPaxosPromisePersistence() throws Exception {
        PaxosPromise expected;
        PaxosPromiseProto persisted;
        PaxosPromise actual;

        expected = PaxosPromise.reject(new PaxosProposalId(3, "unique"));
        persisted = PaxosPromises.toProto(expected);
        actual = PaxosPromises.fromProto(persisted);
        assertEquals(expected, actual);

        expected = PaxosPromise.accept(new PaxosProposalId(20, "id"), new PaxosProposalId(6, "fire"), new PaxosValue("me", 5, new byte[]{8, 8, 100}));
        persisted = PaxosPromises.toProto(expected);
        actual = PaxosPromises.fromProto(persisted);
        assertEquals(expected, actual);

        expected = PaxosPromise.accept(new PaxosProposalId(20, "id"), null, new PaxosValue("me", 5, new byte[]{8, 8, 100}));
        persisted = PaxosPromises.toProto(expected);
        actual = PaxosPromises.fromProto(persisted);
        assertEquals(expected, actual);

        expected = PaxosPromise.accept(new PaxosProposalId(20, "id"), null, null);
        persisted = PaxosPromises.toProto(expected);
        actual = PaxosPromises.fromProto(persisted);
        assertEquals(expected, actual);
    }

    @Test
    public void testPaxosResponsePersistence() throws Exception {
        PaxosResponse expected;
        PaxosAcceptorPersistence.PaxosResponse persisted;
        PaxosResponse actual;

        expected = new PaxosResponseImpl(true);
        persisted = PaxosResponses.toProto(expected);
        actual = PaxosResponses.fromProto(persisted);
        assertEquals(expected, actual);

        expected = new PaxosResponseImpl(false);
        persisted = PaxosResponses.toProto(expected);
        actual = PaxosResponses.fromProto(persisted);
        assertEquals(expected, actual);
    }
}
