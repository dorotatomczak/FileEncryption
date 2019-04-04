package main;


public class DecryptionDetails {
	private String mode;
	private String sessionKey;
	
	// TODO dodac pola
	public DecryptionDetails() {

	}

	public DecryptionDetails(String mode, String sessionKey) {
		super();
		this.mode = mode;
		this.sessionKey = sessionKey;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

}

