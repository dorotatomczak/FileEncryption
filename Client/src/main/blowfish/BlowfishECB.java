package main.blowfish;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import main.DecryptionDetails;

public class BlowfishECB extends Blowfish{

	public BlowfishECB(DecryptionDetails dDetails) throws Exception {
		super(dDetails);

		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		cipher = Cipher.getInstance(dDetails.getMode());
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
	}

}
