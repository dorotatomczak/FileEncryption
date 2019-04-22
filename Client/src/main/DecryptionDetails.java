package main;

import java.util.List;

public class DecryptionDetails {
	private String mode;
	private List<Receiver> receivers;
	private String vector;
	
	// TODO dodac pola
	public DecryptionDetails() {

	}

	public DecryptionDetails(String mode, List<Receiver> receivers, String vector) {
		super();
		this.mode = mode;
		this.receivers = receivers;
		this.vector = vector;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}	

	public List<Receiver> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}


}

