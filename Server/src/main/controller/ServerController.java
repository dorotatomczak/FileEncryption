package main.controller;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.fxml.FXML;

import javafx.scene.control.TextField;

import javafx.scene.control.Label;

import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import main.MainThread;

public class ServerController {
	@FXML
	private TextField fileTextField;
	@FXML
	private Label infoLabel;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	//TODO Dodac info co sie obecnie dzieje na serwerze
	
	@FXML
	public void initialize(){
		executor.submit(new MainThread());
		fileTextField.setText(MainThread.fileToEncrypt.getAbsolutePath());
	}

	// Event Listener on TextField[#fileTextField].onMouseClicked
	@FXML
	public void onChangeFileClick(MouseEvent event) {
        File file = new FileChooser().showOpenDialog(null);
        if (file == null){
            return;
        }
        MainThread.fileToEncrypt = file;
        fileTextField.setText(file.getAbsolutePath());
	}
}
