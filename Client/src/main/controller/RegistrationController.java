package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import main.database.UserDao;
import main.util.DialogUtils;
import main.util.PasswordUtils;
import main.util.RSAKeysUtils;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class RegistrationController {
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField repeatPasswordField;
	@FXML
	private Button registerButton;
	@FXML
	private Button loginButton;

	// Event Listener on Button[#registerButton].onAction
	@FXML
	public void onRegisterButtonClick(ActionEvent event) {

		// TODO w nowym w�tku przeprowadzi� te operacje, w widoku doda� loading
		// indicator i informacje, co si� teraz robi
		if (!isInputValid()) {
			return;
		}
		
		String login = loginField.getText();

		String salt = PasswordUtils.generateSalt().get();
		String securePassword = PasswordUtils.hashPassword(login, salt).get();
		String saltyhash = salt + securePassword;

		if (new UserDao().insertUser(login, saltyhash)) {
			
			Stage stage = (Stage) registerButton.getScene().getWindow();
			SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Login.fxml"));
		}

	}

	// Event Listener on Button[#loginButton].onAction
	@FXML
	public void onLoginButtonClick(ActionEvent event) {
		Stage stage = (Stage) loginButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Login.fxml"));
	}

	private boolean isInputValid() {

		String login = loginField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();

		if (!login.matches("[A-Za-z0-9]{4,30}")) {
			DialogUtils.showDialog("Nieprawid�owy login",
					"Login musi mie� od 4 do 30 znak�w. " + "Dozwolone s� du�e i ma�e litery oraz cyfry.",
					AlertType.INFORMATION);
			return false;
		}
		if (new UserDao().getUserByLogin(login) != null) {
			DialogUtils.showDialog("Nieprawid�owy login", "Login jest ju� zaj�ty.", AlertType.INFORMATION);
			return false;
		}
		if (password.length() < 4) {
			DialogUtils.showDialog("Nieprawid�owe has�o", "Has�o musi mie� >= 4 znaki.", AlertType.INFORMATION);
			return false;
		}
		if (repeatPassword.length() < 4 || !password.equals(repeatPassword)) {
			DialogUtils.showDialog("Nieprawid�owe has�o", "Has�a nie s� takie same.", AlertType.INFORMATION);
			return false;
		}

		return true;
	}
}
