package blowfish;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishCBC extends Blowfish {
	
	private static final int VECTOR_SIZE = 8;
	
	private byte[] vector;

	public BlowfishCBC(String mode) throws Exception {
		super();
		
		vector = new byte[VECTOR_SIZE];
		SecureRandom srandom = new SecureRandom();
		srandom.nextBytes(vector);
		System.out.println(bytesToHex(vector));

		IvParameterSpec ivSpec = new IvParameterSpec(vector);
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		cipher = Cipher.getInstance(mode);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	}
	
	public String getVector() {
		return Base64.getEncoder().encodeToString(vector);
	}

}
