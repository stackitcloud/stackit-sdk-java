package cloud.stackit.sdk.core.model;

import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class ServiceAccountKey {
    private final String id;
    private final String publicKey;
    private final Date created;
    private final String keyType;
    private final String keyOrigin;
    private final String keyAlgorithm;
    private final boolean active;
    private final Date validUntil;
    private final ServiceAccountCredentials credentials;

    public ServiceAccountKey(String id, String publicKey, Date created, String keyType, String keyOrigin, String keyAlgorithm, boolean active, Date validUntil, ServiceAccountCredentials credentials) {
        this.id = id;
        this.publicKey = publicKey;
        this.created = created;
        this.keyType = keyType;
        this.keyOrigin = keyOrigin;
        this.keyAlgorithm = keyAlgorithm;
        this.active = active;
        this.validUntil = validUntil;
        this.credentials = credentials;
    }

    public String getId() {
        return id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Date getCreated() {
        return created;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getKeyOrigin() {
        return keyOrigin;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public boolean isActive() {
        return active;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public ServiceAccountCredentials getCredentials() {
        return credentials;
    }

    public RSAPublicKey getPublicKeyParsed() {
        RSAPublicKey pubKey = null;
        try {
            String trimmedKey = publicKey.replaceFirst("-----BEGIN PUBLIC KEY-----", "");
            trimmedKey = trimmedKey.replaceFirst("-----END PUBLIC KEY-----", "");
            trimmedKey = trimmedKey.replaceAll("\n","");

            byte[] publicBytes = Base64.getDecoder().decode(trimmedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return pubKey;
    }

    public static ServiceAccountKey loadCredentials(String json) throws com.google.gson.JsonSyntaxException {
        return new Gson().fromJson(json, ServiceAccountKey.class);
    }
}
