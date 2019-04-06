package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.CipherOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class DecryptTask extends Task<Void> {

	private File file;

	public DecryptTask(File file) {
		this.file = file;
	}

	@Override
	protected Void call() throws Exception {

		DecryptionDetails dDetails;
		try {
			dDetails = extractDetailsFromFile();
		} catch (Exception e) {
			return null;
		}
		Blowfish blowfish = new Blowfish(dDetails);

		// odszyfruj plik
		try (FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)){
		
			bufferedInputStream.skip(dDetails.getSkipBytes());
			
			byte[] buffer = new byte[4096];
			int readSize;
			File encFile = new File("./decrypted/", file.getName());
			file.createNewFile();
			try (OutputStream outputStream = new BufferedOutputStream(
					new CipherOutputStream(new FileOutputStream(encFile), blowfish.getCipher()))) {

				while ((readSize = bufferedInputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, readSize);
				}

				System.out.println("Client decrypted file");
			}
		}

		return null;
	}

	private DecryptionDetails extractDetailsFromFile() throws Exception {
		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			Gson gson = new Gson();
			String jsonString = bufferedReader.readLine();
			DecryptionDetails dDetails = gson.fromJson(jsonString, DecryptionDetails.class);
			dDetails.setSkipBytes(jsonString.getBytes().length+2);

			return dDetails;
		}
	}

}
