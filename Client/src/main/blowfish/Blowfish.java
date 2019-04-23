package main.blowfish;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import main.DecryptionDetails;

public class Blowfish extends BlowfishBase{

	public Blowfish(DecryptionDetails dDetails) throws Exception {
		super(dDetails);
		byte[] vector = Base64.getDecoder().decode(dDetails.getVector());
		
		IvParameterSpec ivSpec = new IvParameterSpec(vector);
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		cipher = Cipher.getInstance(dDetails.getMode());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	}

}
