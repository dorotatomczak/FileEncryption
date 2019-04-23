package main;

import java.util.List;

public class EncryptionDetails {
	private String mode;
	private String fileName;
	private List<Receiver> receivers;

	public EncryptionDetails() {

	}

	public EncryptionDetails(String mode, List<Receiver> receivers, String fileName) {
		super();
		this.mode = mode;
		this.receivers = receivers;
		this.fileName = fileName;
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Receiver> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}
}
