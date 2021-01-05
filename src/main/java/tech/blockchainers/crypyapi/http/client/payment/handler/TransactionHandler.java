package tech.blockchainers.crypyapi.http.client.payment.handler;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TransactionHandler {

    private final Web3j web3j;

    public TransactionHandler(Web3j web3j) {
        this.web3j = web3j;
    }

    public String sendTransaction(Credentials credentials, int amount, String trxId, String serviceAddress) throws ExecutionException, InterruptedException, IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        RawTransaction trx =
                RawTransaction.createTransaction(
                        nonce,
                        DefaultGasProvider.GAS_PRICE.divide(BigInteger.valueOf(4)),
                        DefaultGasProvider.GAS_LIMIT,
                        serviceAddress,
                        BigInteger.valueOf(amount),
                        Numeric.toHexString(trxId.getBytes(StandardCharsets.UTF_8)));
        byte[] signedTrx = TransactionEncoder.signMessage(trx, credentials);

        String hexValue = Numeric.toHexString(signedTrx);
        EthSendTransaction res = web3j.ethSendRawTransaction(hexValue).send();
        waitForTransaction(res.getTransactionHash());
        return res.getTransactionHash();
    }

    private void waitForTransaction(String trxHash) throws IOException, InterruptedException {
        while (true) {
            EthGetTransactionReceipt transactionReceipt = web3j
                    .ethGetTransactionReceipt(trxHash)
                    .send();
            if (transactionReceipt.getResult() != null) {
                break;
            }
            log.debug("Waiting for transaction {} to be mined.", trxHash);
            Thread.sleep(100);
        }
    }
}
