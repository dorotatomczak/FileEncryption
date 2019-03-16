package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import main.ConnectTask;

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
		Task<Void> connectTask =  new ConnectTask();
		executor.submit(connectTask);
	}
}
