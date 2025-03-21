// SPDX-License-Identifier: Apache-2.0
package com.hedera.hashgraph.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import com.hedera.hashgraph.sdk.proto.ResponseCodeEnum;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReceiptStatusExceptionTest {

    @Test
    void shouldHaveMessage() {
        var validStart = Instant.ofEpochSecond(1554158542);
        var txId = new TransactionId(new AccountId(0, 0, 100), validStart);
        var txReceipt = TransactionReceipt.fromProtobuf(com.hedera.hashgraph.sdk.proto.TransactionReceipt.newBuilder()
                .setStatusValue(ResponseCodeEnum.INSUFFICIENT_TX_FEE_VALUE)
                .build());
        var e = new ReceiptStatusException(txId, txReceipt);

        assertThat(e.getMessage())
                .isEqualTo("receipt for transaction 0.0.100@1554158542.000000000 raised status INSUFFICIENT_TX_FEE");
    }
}
