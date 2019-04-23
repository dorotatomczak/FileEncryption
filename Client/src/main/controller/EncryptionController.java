package main.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.scene.control.ListView;

import javafx.scene.control.ProgressBar;

import javafx.scene.control.Label;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.EncryptTask;
import main.database.User;
import main.database.UserDao;
import main.util.DialogUtils;
import main.util.LoggedInUser;
import javafx.scene.control.ChoiceBox;

public class EncryptionController {
	@FXML
	private Button logoutButton;
	@FXML
	private Button decryptButton;
	@FXML
	private TextField outputLabel;
	@FXML
	private ChoiceBox<String> modeChoiceBox;
	@FXML
	private ChoiceBox<String> blockSizeChoiceBox;
	@FXML
	private ListView<User> usersList;
	@FXML
	private ListView<User> receiversList;
	@FXML
	private Button encryptButton;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	//TODO zmienic labels na ikonki
	@FXML
	private Label add;
	@FXML
	private Label remove;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private ObservableList<String> modes = FXCollections.observableArrayList("ECB", "CBC", "CFB", "OFB");
	private ObservableList<String> blockSizes = FXCollections.observableArrayList("8", "16", "24", "32", "64");
	private String selectedMode = null;
	private boolean blockSizeEnabled = false;

	@FXML
	public void initialize(){	
		
		modeChoiceBox.setItems(modes);
		modeChoiceBox.getSelectionModel().selectedIndexProperty()
		.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				selectedMode = modes.get((int) arg2);
				if (selectedMode.equals("CFB") || selectedMode.equals("OFB")) {
					enableBlockSizeSelection();
				}else {
					disableBlockSizeSelection();
				}
			}
		});

	blockSizeChoiceBox.setItems(blockSizes);

	Set<User> users = new UserDao().getAllUsers();
	usersList.getItems().addAll(users);

	outputLabel.focusedProperty().addListener((arg0,oldValue,newValue)->
	{
		if (!newValue) { // when focus lost
			if (containsIllegals(outputLabel.getText())) {
				outputLabel.setText("");
				DialogUtils.showDialog("Nazwa pliku wynikowego", "Nazwa pliku jest niepoprawna", AlertType.INFORMATION);
			}
		}
	});
	}

	// Event Listener on Button[#logoutButton].onAction
	@FXML
	public void logout(ActionEvent event) {
		LoggedInUser.logout();
		Stage stage = (Stage) logoutButton.getScene().getWindow();
		stage.setTitle("");
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Login.fxml"));
	}

	// Event Listener on Button[#decryptButton].onAction
	@FXML
	public void decrypt(ActionEvent event) {
		Stage stage = (Stage) logoutButton.getScene().getWindow();
		SceneSwitcher.switchScene(stage, getClass().getResource("../resource/Decryption.fxml"));
	}

	// Event Listener on Button[#encryptButton].onAction
	@FXML
	public void encrypt(ActionEvent event) {
		Task<Void> encryptTask;
		
		if (outputLabel.getText() == null || outputLabel.getText().trim().isEmpty() ||
				selectedMode == null || (blockSizeEnabled && blockSizeChoiceBox.getSelectionModel().isEmpty()) ||
				receiversList.getItems().size() < 1) {
			DialogUtils.showDialog("Error", "Uzupelnij wszystkie pola", AlertType.INFORMATION);
			return;
		}
		
		List<User> receivers = receiversList.getItems();
		if (blockSizeEnabled) {
			String mode = selectedMode+blockSizeChoiceBox.getValue();
			encryptTask =  new EncryptTask(buildMode(mode), outputLabel.getText(), receivers);
		}
		else {
			encryptTask =  new EncryptTask(buildMode(selectedMode), outputLabel.getText(), receivers);
		}
		
		executor.submit(encryptTask);
		progressLabel.textProperty().bind(encryptTask.messageProperty());
		progressBar.progressProperty().bind(encryptTask.progressProperty());
	}
	
	@FXML
	public void addReceiver(MouseEvent event) {
		User user = usersList.getSelectionModel().getSelectedItem();
		if (user != null) {
			receiversList.getItems().add(user);
			usersList.getItems().remove(user);
		}
	}
	
	@FXML
	public void removeReceiver(MouseEvent event) {
		User user = receiversList.getSelectionModel().getSelectedItem();
		if (user != null) {
			usersList.getItems().add(user);
			receiversList.getItems().remove(user);
		}
	}

	private boolean containsIllegals(String toExamine) {
	    Pattern pattern = Pattern.compile("[~#@*+%{}.<>\\[\\]|\"\\_^]");
	    Matcher matcher = pattern.matcher(toExamine);
	    return matcher.find();
	}

	private String buildMode(String mode) {
		return "Blowfish/"+mode+"/PKCS5Padding";
	}
	
	private void enableBlockSizeSelection() {
		blockSizeEnabled = true;
		blockSizeChoiceBox.setDisable(false);
	}
	
	private void disableBlockSizeSelection() {
		blockSizeEnabled = false;
		blockSizeChoiceBox.setDisable(true);
	}
}
