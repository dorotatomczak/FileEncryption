package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import main.database.User;
import main.database.UserDao;
import main.util.DialogUtils;
import main.util.LoggedInUser;
import main.util.PasswordUtils;

public class LoginController {
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button registerButton;

	// Event Listener on Button[#loginButton].onAction
	@FXML
	public void onLoginButtonClick(ActionEvent event) {
		
		// TODO w nowym w¹tku przeprowadziæ te operacje, w widoku dodaæ loading
		// indicator i informacje, co siê teraz robi
		
		User user = new UserDao().getUserByLogin(loginField.getText());
		
		if(user == null) {
			DialogUtils.showDialog("B³¹d logowania", "Niepoprawny login lub has³o.", AlertType.INFORMATION);
			return;
		}
		
		String saltyhash = user.getPassword();
		String salt = saltyhash.substring(0, 44);
		String hash = saltyhash.substring(44);
		
		if (PasswordUtils.verifyPassword(passwordField.getText(), hash, salt)) {
			LoggedInUser.loggedInUser = user;
			Stage stage = (Stage) loginButton.getScene().getWindow();
			SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Main.fxml"));
		}
		else {
			DialogUtils.showDialog("B³¹d logowania", "Niepoprawny login lub has³o.", AlertType.INFORMATION);
		}
		
	}
	// Event Listener on Button[#registerButton].onAction
	@FXML
	public void onRegisterButtonClick(ActionEvent event) {
		Stage stage = (Stage) registerButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Registration.fxml"));
	}
}
