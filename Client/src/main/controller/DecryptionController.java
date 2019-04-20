package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.DecryptTask;
import main.util.LoggedInUser;

public class DecryptionController {
	@FXML
	private Button encryptButton;
	@FXML
	private Button logoutButton;
	@FXML
	private TextField inputLabel;
	@FXML
	private Button decryptButton;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();;
	private File file;
	private FileChooser fileChooser; 
	
	@FXML
	public void initialize(){
		file = null;
		File initialDirectory = new File("..\\Client\\encrypted");
		fileChooser =  new FileChooser();
		fileChooser.setInitialDirectory(initialDirectory);
	}

	// Event Listener on Button[#encryptButton].onAction
	@FXML
	public void encrypt(ActionEvent event) {
		Stage stage = (Stage) logoutButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Encryption.fxml"));
	}
	// Event Listener on Button[#logoutButton].onAction
	@FXML
	public void logout(ActionEvent event) {
		LoggedInUser.logout();
		Stage stage = (Stage) logoutButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Login.fxml"));
	}
	// Event Listener on TextField[#intputLabel].onMouseClicked
	@FXML
	public void onInputClick(MouseEvent event) {
        file = fileChooser.showOpenDialog(null);
        if (file == null){
            return;
        }
        inputLabel.setText(file.getAbsolutePath());
	}
	// Event Listener on Button[#decryptButton].onAction
	@FXML
	public void decrypt(ActionEvent event) {
		if (file != null) {
			Task<Void> decryptTask =  new DecryptTask(file);
			progressLabel.textProperty().bind(decryptTask.messageProperty());
			progressBar.progressProperty().bind(decryptTask.progressProperty());
			executor.submit(decryptTask);
		}
	}
}
