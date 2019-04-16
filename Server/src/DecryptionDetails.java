
public class DecryptionDetails {
	private String mode;
	private String sessionKey;
	private String vector;
	
	// TODO dodac pola
	public DecryptionDetails() {

	}

	public DecryptionDetails(String mode, String sessionKey, String vector) {
		super();
		this.mode = mode;
		this.sessionKey = sessionKey;
		this.vector = vector;
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

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}

}
