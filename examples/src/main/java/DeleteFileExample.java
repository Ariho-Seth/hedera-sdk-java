import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.FileCreateTransaction;
import com.hedera.hashgraph.sdk.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.FileInfoQuery;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionReceipt;

import com.hedera.hashgraph.sdk.TransactionResponse;
import io.github.cdimascio.dotenv.Dotenv;

public final class DeleteFileExample {

    // see `.env.sample` in the repository root for how to specify these values
    // or set environment variables with the same names
    private static final AccountId OPERATOR_ID = AccountId.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_ID")));
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_KEY")));

    private DeleteFileExample() { }

    public static void main(String[] args) throws HederaPreCheckStatusException, TimeoutException, HederaReceiptStatusException {
        // `Client.forMainnet()` is provided for connecting to Hedera mainnet
        Client client = Client.forTestnet();

        // Defaults the operator account ID and key such that all generated transactions will be paid for
        // by this account and be signed by this key
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);

        // The file is required to be a byte array,
        // you can easily use the bytes of a file instead.
        byte[] fileContents = "Hedera hashgraph is great!".getBytes(StandardCharsets.UTF_8);

        TransactionResponse transactionResponse = new FileCreateTransaction()
            .setKeys(OPERATOR_KEY)
            .setContents(fileContents)
            .setMaxTransactionFee(new Hbar(2))
            .execute(client);

        TransactionReceipt receipt = transactionResponse.getReceipt(client);
        FileId newFileId = Objects.requireNonNull(receipt.fileId);

        System.out.println("file: " + newFileId);

        // now delete the file
        TransactionResponse fileDeleteTransactionResponse = new FileDeleteTransaction()
            .setFileId(newFileId)
            .execute(client);

        // if this doesn't throw then the transaction was a success
        fileDeleteTransactionResponse.getReceipt(client);

        System.out.println("File deleted successfully.");

        new FileInfoQuery()
            .setFileId(newFileId)
            .execute(client);

        // note the above fileInfo will fail with FILE_DELETED due to a known issue on Hedera

    }
}
