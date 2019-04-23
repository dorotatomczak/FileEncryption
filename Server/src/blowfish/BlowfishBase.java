package blowfish;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import main.RSA;

public class BlowfishBase {

	/*
	 * You should use 24 or 32 byte for higher security, but only if you installed
	 * the Unlimited Strength policy files for your JRE/JDK.
	 */
	static final int SESSION_KEY_SIZE = 16;

	protected Cipher cipher;
	protected byte[] key;

	public Cipher getCipher() {
		return cipher;
	}
	
	public String encryptKey(PublicKey rsaPublicKey) throws Exception {
        return Base64.getEncoder().encodeToString(RSA.encrypt(rsaPublicKey, key));
	}
	
	public String encryptString(PublicKey rsaPublicKey) throws Exception{
        Cipher rsa;
        rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        return Base64.getEncoder().encodeToString(rsa.doFinal(key));
	}

	public BlowfishBase() throws Exception {

		key = generateSessionKey();
	}
	
	private byte[] generateSessionKey() throws NoSuchAlgorithmException {
		/*
		 * getInstanceStrong() returns an instance of the strongest SecureRandom
		 * implementation available on each platform
		 */
		SecureRandom r = SecureRandom.getInstanceStrong();
		r.setSeed(System.currentTimeMillis());
		byte[] sessionKey = new byte[SESSION_KEY_SIZE];
		r.nextBytes(sessionKey);
		System.out.println(bytesToHex(sessionKey));
		return sessionKey;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	// Converts byte array to hex string
	// From:
	// http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static BlowfishBase getBlowfish(String fullMode) throws Exception {
		
		String mode = fullMode.split("/")[1];
		
		switch (mode) {
		case "ECB":
			return new BlowfishECB(fullMode);
		default:
			return new Blowfish(fullMode);
		}
	}
	
	
}
