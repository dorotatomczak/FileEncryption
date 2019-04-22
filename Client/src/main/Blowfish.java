package main;

import java.util.Base64;
import java.util.List;
import java.util.Random;

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

		byte[] key;
		String stringKey = retrieveSessionKey(dDetails.getReceivers());
		if (stringKey == null) {
			key = generateFakeKey();
		}else {
			key = RSAKeysUtils.decrypt(LoggedInUser.loggedInUser, stringKey);
		}

		byte[] vector = Base64.getDecoder().decode(dDetails.getVector());
		
		System.out.println(RSAKeysUtils.bytesToHex(key));
		System.out.println(RSAKeysUtils.bytesToHex(vector));
		
		IvParameterSpec ivSpec = new IvParameterSpec(vector);
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		cipher = Cipher.getInstance(dDetails.getMode());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

	}
	
	private String retrieveSessionKey(List<Receiver> receivers) {
		for (Receiver receiver : receivers) {
			if (receiver.getLogin().equals(LoggedInUser.loggedInUser.getLogin())) {
				return receiver.getKey();
			}
		}
		return null;
	}
	
	private byte[] generateFakeKey() {
		Random r = new Random();
		byte[] fakeKey = new byte[16];
		r.nextBytes(fakeKey);
		return fakeKey;
	}

}
