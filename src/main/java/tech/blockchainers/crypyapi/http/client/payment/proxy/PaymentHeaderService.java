package tech.blockchainers.crypyapi.http.client.payment.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import tech.blockchainers.crypyapi.http.client.payment.handler.TransactionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PaymentHeaderService {

    @Value("${service.url}")
    private String serviceUrl;

    private final Credentials senderCredentials;
    private final TransactionHandler transactionHandler;

    public PaymentHeaderService(TransactionHandler transactionHandler, Credentials senderCredentials) {
        this.transactionHandler = transactionHandler;
        this.senderCredentials = senderCredentials;
    }

    public HttpHeaders createPaymentHeaders(String address, String service) throws InterruptedException, ExecutionException, IOException {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = new HashMap<>();
        params.put("address", address); // credentials.getAddress()
        params.put("service", service);
        PaymentDto response = restTemplate.getForObject(serviceUrl + "/setup?address={address}&service={service}", PaymentDto.class, params);
        String trxId = response.getTrxId();
        log.debug("Send payment transaction with data '{}'", trxId);

        String trxHash = transactionHandler.sendTransaction(senderCredentials, response.getAmount(), trxId, response.getServiceAddress());

        String signedTrxId = SignatureUtil.sign(trxId, senderCredentials);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        headers.set("CPA-Signed-Identifier", signedTrxId);
        headers.set("CPA-Transaction-Hash", trxHash);
        return headers;
    }
}
