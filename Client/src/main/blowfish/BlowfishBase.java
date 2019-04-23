package main.blowfish;

import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;

import main.DecryptionDetails;
import main.Receiver;
import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class BlowfishBase {
	
	protected Cipher cipher;
	protected byte[] key;

	public Cipher getCipher() {
		return cipher;
	}

	public BlowfishBase(DecryptionDetails dDetails) throws Exception {

		String stringKey = retrieveSessionKey(dDetails.getReceivers());
		if (stringKey == null) {
			key = generateFakeKey();
		}else {
			key = RSAKeysUtils.decrypt(LoggedInUser.loggedInUser, stringKey);
		}
	}
	
	// check if logged in user is on he list of receivers. If so, get its encrypted session key
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
	
	public static BlowfishBase getBlowfish(DecryptionDetails dDetails) throws Exception {
		
		String mode = dDetails.getMode().split("/")[1];
		
		switch (mode) {
		case "ECB":
			return new BlowfishECB(dDetails);
		default:
			return new Blowfish(dDetails);
		}
	}

}
