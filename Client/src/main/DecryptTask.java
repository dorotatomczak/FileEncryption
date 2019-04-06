package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.crypto.CipherOutputStream;

import com.google.gson.Gson;

import javafx.concurrent.Task;

public class DecryptTask extends Task<Void> {

	private File file;

	public DecryptTask(File file) {
		this.file = file;
	}

	@Override
	protected Void call() throws Exception {
		
		// odszyfruj plik
		try (FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

			byte[] jsonSizeInBytes = new byte[4];
			bufferedInputStream.read(jsonSizeInBytes);
			int jsonSize = ByteBuffer.wrap(jsonSizeInBytes).getInt();

			byte[] jsonInBytes = new byte[jsonSize];
			bufferedInputStream.read(jsonInBytes);
			DecryptionDetails dDetails = bytesToJson(jsonInBytes);
			
			Blowfish blowfish = new Blowfish(dDetails);

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
	
	private DecryptionDetails bytesToJson(byte[] data) throws Exception {

		Gson gson = new Gson();
		String jsonString = new String(data);
		DecryptionDetails dDetails = gson.fromJson(jsonString, DecryptionDetails.class);
		return dDetails;
	}

}
