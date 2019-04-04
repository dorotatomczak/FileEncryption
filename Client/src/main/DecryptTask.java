package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import javafx.concurrent.Task;
import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class DecryptTask extends Task<Void>  {
	
	private File file;
	
	public DecryptTask(File file) {
		this.file = file;
	}

	@Override
	protected Void call() throws Exception {
		
		try (FileReader fileReader = new FileReader(file);
				 BufferedReader bufferedReader = new BufferedReader(fileReader)){
			Gson gson =  new Gson();
			String jsonString = bufferedReader.readLine();

			DecryptionDetails dDetails = gson.fromJson(jsonString, DecryptionDetails.class);
		
			//Zalogowany u¿ytkownik dokonuje próby odszyfrowania klucza sesyjnego swoim kluczem prywatnym
			byte[] sessionKey = RSAKeysUtils.decryptKey(LoggedInUser.loggedInUser, dDetails.getSessionKey());
			System.out.println(RSAKeysUtils.bytesToHex(sessionKey));
			
		}
		
		return null;
	}

}
