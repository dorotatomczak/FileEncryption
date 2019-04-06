package main;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class Blowfish {
	
	private Cipher cipher;

	public Cipher getCipher() {
		return cipher;
	}

	public Blowfish(DecryptionDetails dDetails) throws Exception {

		
		byte[] key = RSAKeysUtils.decryptKey(LoggedInUser.loggedInUser, dDetails.getSessionKey());
		byte[] vector = Base64.getDecoder().decode(dDetails.getVector());
		
		System.out.println(RSAKeysUtils.bytesToHex(key));
		System.out.println(RSAKeysUtils.bytesToHex(vector));
		
		IvParameterSpec ivSpec = new IvParameterSpec(vector);
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		cipher = Cipher.getInstance(dDetails.getMode());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

	}

}
