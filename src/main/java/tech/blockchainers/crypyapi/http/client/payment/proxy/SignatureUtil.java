package tech.blockchainers.crypyapi.http.client.payment.proxy;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SignatureUtil {

    public static String sign(String message, Credentials credentials) {
        Sign.SignatureData signature = Sign.signPrefixedMessage(Hash.sha3(message.getBytes(StandardCharsets.UTF_8)), credentials.getEcKeyPair());
        ByteBuffer sigBuffer = ByteBuffer.allocate(signature.getR().length + signature.getS().length + 1);
        sigBuffer.put(signature.getR());
        sigBuffer.put(signature.getS());
        sigBuffer.put(signature.getV());
        return Numeric.toHexString(sigBuffer.array());
    }

}