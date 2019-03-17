package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.database.User;
import main.database.UserDao;
import main.util.RSAKeysUtils;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("resource/Login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("resource/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			/*User user = new UserDao().getUserByLogin("test1");
			RSAKeysUtils.generateRSAKeys(user);
			RSAKeysUtils.decryptPrivateKey(user);*/
			
		} catch(Exception e) {;
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}