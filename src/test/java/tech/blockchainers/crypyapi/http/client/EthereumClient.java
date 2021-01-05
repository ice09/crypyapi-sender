package tech.blockchainers.crypyapi.http.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import tech.blockchainers.crypyapi.http.client.config.ApplicationConfiguration;
import tech.blockchainers.crypyapi.http.client.payment.proxy.PaymentHeaderService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootTest(classes = ApplicationConfiguration.class)
@TestPropertySource("/test.properties")
public class EthereumClient {

    @Autowired
    private PaymentHeaderService paymentHeaderService;

    @Autowired
    private Credentials senderCredentials;

    @Value("${service.url}")
    private String serviceUrl;

    @Test
    // Make sure address 0x7ff67156e5236B51b4F9c7dEf3878cFc7C6c33f7 has POA in Sokol Testnet before running the Test!
    public void shouldCallCompletePaymentFlow() throws InterruptedException, ExecutionException, IOException {
        HttpHeaders headers = paymentHeaderService.createPaymentHeaders(senderCredentials.getAddress(), "jokeRequestService");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> joke = restTemplate.exchange(serviceUrl + "/request", HttpMethod.GET, entity, String.class);
        log.info(("Hold On: {}").toUpperCase(), joke.getBody().toUpperCase());
    }

}
