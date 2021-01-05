package tech.blockchainers.crypyapi.http.client.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import tech.blockchainers.crypyapi.http.client.payment.handler.TransactionHandler;
import tech.blockchainers.crypyapi.http.client.payment.proxy.CredentialsUtil;
import tech.blockchainers.crypyapi.http.client.payment.proxy.PaymentHeaderService;

import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationConfiguration {

    @Value("${ethereum.rpc.url}")
    private String ethereumRpcUrl;

    @Value("${sender.private.key}")
    private String senderPrivateKey;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(ethereumRpcUrl, createOkHttpClient()));
    }

    @Bean
    public Credentials createCredentials() {
        return CredentialsUtil.createFromPrivateKey(senderPrivateKey);
    }

    @Bean
    public TransactionHandler createTransactionHandler() {
        return new TransactionHandler(web3j());
    }

    @Bean
    public PaymentHeaderService createPaymentHeaderService() {
        return new PaymentHeaderService(createTransactionHandler(), createCredentials());
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configureTimeouts(builder);
        return builder.build();
    }

    private void configureTimeouts(OkHttpClient.Builder builder) {
        long tos = 8000L;
        builder.connectTimeout(tos, TimeUnit.SECONDS);
        builder.readTimeout(tos, TimeUnit.SECONDS);  // Sets the socket timeout too
        builder.writeTimeout(tos, TimeUnit.SECONDS);
    }

}
