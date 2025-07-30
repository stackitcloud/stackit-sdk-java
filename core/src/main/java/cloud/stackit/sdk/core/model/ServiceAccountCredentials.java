package cloud.stackit.sdk.core.model;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

public class ServiceAccountCredentials {
    private final String aud;
    private final String iss;
    private final String kid;
    private String privateKey;
    private final UUID sub;

    public ServiceAccountCredentials(String aud, String iss, String kid, String privateKey, UUID sub) {
        this.aud = aud;
        this.iss = iss;
        this.kid = kid;
        this.privateKey = privateKey;
        this.sub = sub;
    }

    public String getAud() {
        return aud;
    }

    public String getIss() {
        return iss;
    }

    public String getKid() {
        return kid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isPrivateKeySet() {
        return !privateKey.trim().isEmpty();
    }

    public UUID getSub() {
        return sub;
    }

    public RSAPrivateKey getPrivateKeyParsed() {
        RSAPrivateKey prvKey = null;
        try {
            String trimmedKey = privateKey.replaceFirst("-----BEGIN PRIVATE KEY-----", "");
            trimmedKey = trimmedKey.replaceFirst("-----END PRIVATE KEY-----", "");
            trimmedKey = trimmedKey.replaceAll("\n","");

            byte[] privateBytes = Base64.getDecoder().decode(trimmedKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            prvKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return prvKey;
    }
}
