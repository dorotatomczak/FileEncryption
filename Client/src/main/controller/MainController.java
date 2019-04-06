package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import main.DecryptTask;
import main.EncryptTask;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

public class MainController {
	@FXML
	private Button encryptButton;
	@FXML
	private Button decryptButton;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	// Event Listener on Button[#encryptButton].onAction
	@FXML
	public void encryptFile(ActionEvent event) {
        File file = new FileChooser().showOpenDialog(null);
        if (file == null){
            return;
        }
        // TODO odczyt wybranego trybu i dlugosci podbloku dla trybow cfb i ofb
		Task<Void> encryptTask =  new EncryptTask(file, "Blowfish/CBC/PKCS5Padding");
		executor.submit(encryptTask);
	}
	
	// Event Listener on Button[#decryptButton].onAction
	@FXML
	public void decryptFile(ActionEvent event) {
		//TODO set initial directory na encrypted
        File file = new FileChooser().showOpenDialog(null);
        if (file == null){
            return;
        }
		Task<Void> decryptTask =  new DecryptTask(file);
		executor.submit(decryptTask);
	}
}
