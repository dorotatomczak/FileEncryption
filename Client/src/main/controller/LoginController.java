package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import main.util.DialogUtils;
import main.util.PasswordUtils;
import database.DatabaseConnection;
import database.User;
import javafx.event.ActionEvent;

import javafx.scene.control.PasswordField;
import database.UserDao;

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
		User user = new UserDao().getUserByLogin(loginField.getText());
		String saltyhash = user.getPassword();
		String salt = saltyhash.substring(0, 44);
		String hash = saltyhash.substring(44);
		
		System.out.println("rejestracja, salt: "+salt+"length: "+salt.length());
		System.out.println("rejestracja, securepass: "+hash+"length: "+hash.length());
		System.out.println("rejestracja, saltyhash: "+saltyhash+"length: "+saltyhash.length());
		
		if (PasswordUtils.verifyPassword(passwordField.getText(), hash, salt)) {
			// TODO przekierowanie do okna g³ównego aplikacji
		}
		else {
			DialogUtils.showDialog("B³¹d logowania", "Niepoprawny login lub has³o.", AlertType.INFORMATION);
		}
	}
	// Event Listener on Button[#registerButton].onAction
	@FXML
	public void onRegisterButtonClick(ActionEvent event) {
		Stage stage = (Stage) registerButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource//Registration.fxml"));
	}
}
