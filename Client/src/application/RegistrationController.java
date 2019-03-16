package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import database.PasswordUtils;
import database.UserDao;
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
		
		if (!isInputValid()) {
			return;
		}
		
		String salt = PasswordUtils.generateSalt(32).get();
		String securePassword = PasswordUtils.hashPassword(loginField.getText(), salt).get();
		String saltyhash = salt+securePassword;
		if (new UserDao().insertUser(loginField.getText(), saltyhash)) {
			//TODO Przekierowanie do logowania czy gdzieœ tam
		}
		
	}
	// Event Listener on Button[#loginButton].onAction
	@FXML
	public void onLoginButtonClick(ActionEvent event) {
		// TODO Przekierowanie do formularza logowania
	}
	
	private boolean isInputValid() {
		
		String login = loginField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();
		
		if (!login.matches("[A-Za-z0-9]{4,30}")) {
			DialogUtils.showDialog("Nieprawid³owy login", "Login musi mieæ od 4 do 30 znaków. "
					+ "Dozwolone s¹ du¿e i ma³e litery oraz cyfry.", AlertType.INFORMATION);
			return false;
		}
		if(new UserDao().getUserByLogin(login)!=null) {
			DialogUtils.showDialog("Nieprawid³owy login", "Login jest ju¿ zajêty.", AlertType.INFORMATION);
			return false;
		}
		if (password.length() < 4) {
			DialogUtils.showDialog("Nieprawid³owe has³o", "Has³o musi mieæ >= 4 znaki.", AlertType.INFORMATION);
			return false;
		}
		if (repeatPassword.length() < 4 || !password.equals(repeatPassword)) {
			DialogUtils.showDialog("Nieprawid³owe has³o", "Has³a nie s¹ takie same.", AlertType.INFORMATION);
			return false;
		}
		
		return true;
	}
}
