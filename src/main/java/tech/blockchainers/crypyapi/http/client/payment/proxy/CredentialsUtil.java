package tech.blockchainers.crypyapi.http.client.payment.proxy;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;

@Slf4j
public class CredentialsUtil {

    public static Credentials createFromPrivateKey(String privateKeyHex) {
        return Credentials.create(privateKeyHex);
    }

}
