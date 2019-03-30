package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import main.ConnectTask;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

public class MainController {
	@FXML
	private Button connectButton;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	// Event Listener on Button[#connectButton].onAction
	@FXML
	public void connectToServer(ActionEvent event) {
        File file = new FileChooser().showOpenDialog(null);
        if (file == null){
            return;
        }
        // TODO odczyt wybranego algorytmu, trybu i wielkosci bloku
		Task<Void> connectTask =  new ConnectTask(file, "Blowfish/CBC/NoPadding");
		executor.submit(connectTask);
	}
}
