package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import javafx.concurrent.Task;

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
		}
		
		return null;
	}

}
