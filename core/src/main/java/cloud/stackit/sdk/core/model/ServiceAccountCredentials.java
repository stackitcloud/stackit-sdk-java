package cloud.stackit.sdk.core.model;

import cloud.stackit.sdk.core.utils.Utils;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class ServiceAccountCredentials {
	private final String aud;
	private final String iss;
	private final String kid;
	private String privateKey;
	private final String sub;

	public ServiceAccountCredentials(
			String aud, String iss, String kid, String privateKey, String sub) {
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
		return Utils.isStringSet(privateKey);
	}

	public String getSub() {
		return sub;
	}

	public RSAPrivateKey getPrivateKeyParsed()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String trimmedKey = privateKey.replaceFirst("-----BEGIN PRIVATE KEY-----", "");
		trimmedKey = trimmedKey.replaceFirst("-----END PRIVATE KEY-----", "");
		trimmedKey = trimmedKey.replaceAll("\n", "");

		byte[] privateBytes = Base64.getDecoder().decode(trimmedKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceAccountCredentials that = (ServiceAccountCredentials) o;
		return Objects.equals(aud, that.aud)
				&& Objects.equals(iss, that.iss)
				&& Objects.equals(kid, that.kid)
				&& Objects.equals(privateKey, that.privateKey)
				&& Objects.equals(sub, that.sub);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aud, iss, kid, privateKey, sub);
	}
}
