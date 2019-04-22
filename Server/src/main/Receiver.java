package main;


public class Receiver {
	
	private String login;
	private String key;
	
	public Receiver(String login, String key) {
		super();
		this.login = login;
		this.key = key;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
