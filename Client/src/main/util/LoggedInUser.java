package main.util;

import main.database.User;

public abstract class LoggedInUser {

	public static User loggedInUser = null;
	
	public static void login(User user) {
		loggedInUser = user;
	}
	
	public static void logout() {
		loggedInUser = null;
	}
}
