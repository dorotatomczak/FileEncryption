package main;

public class EncryptionDetails {
	private String mode;
	private String rsaPublicKey;
	private String fileName;
	private long fileSize;

	// TODO dodac pola
	public EncryptionDetails() {

	}

	public EncryptionDetails(String mode, String rsaPublicKey, String fileName, long fileSize) {
		super();
		this.mode = mode;
		this.rsaPublicKey = rsaPublicKey;
		this.fileName = fileName;
		this.fileSize = fileSize;
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
	
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
