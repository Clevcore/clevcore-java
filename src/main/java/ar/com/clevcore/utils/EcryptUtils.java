package ar.com.clevcore.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EcryptUtils {

	private static final Logger log = LoggerFactory.getLogger(EcryptUtils.class);

	// DIGEST ALGORITHM
	public static final String MD2 = "MD2";
	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";

	// CIPHER ALGORITHM
	public static final String AES = "AES";
	public static final String DES = "DES";
	public static final String DESEDE = "DESede";

	private EcryptUtils() {
		throw new AssertionError();
	}

	public static String digestHex(String value, String algorithm) {
		return StringUtils.bytesToHex(digest(value, algorithm));
	}

	public static String digestHex(String value, String algorithm, String charsets) {
		return StringUtils.bytesToHex(digest(value, algorithm, charsets));
	}

	public static String digestBase64(String value, String algorithm) {
		return StringUtils.byteToBase64(digest(value, algorithm));
	}

	public static String digestBase64(String value, String algorithm, String charsets) {
		return StringUtils.byteToBase64(digest(value, algorithm, charsets));
	}

	public static byte[] digest(String value, String algorithm) {
		return digest(value, algorithm, "UTF_8");
	}

	public static byte[] digest(String value, String algorithm, String charsets) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(value.getBytes(charsets));
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			log.error("[E] NoSuchAlgorithmException occurred in [digest]", e);
		} catch (UnsupportedEncodingException e) {
			log.error("[E] UnsupportedEncodingException occurred in [digest]", e);
		}
		return null;
	}

}
