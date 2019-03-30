
public class EncryptionDetails {
	private String mode;
	private String rsaPublicKey;
	private String fileName;

	// TODO dodac pola
	public EncryptionDetails() {

	}

	public EncryptionDetails(String mode, String rsaPublicKey, String fileName) {
		super();
		this.mode = mode;
		this.rsaPublicKey = rsaPublicKey;
		this.fileName = fileName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(String rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
