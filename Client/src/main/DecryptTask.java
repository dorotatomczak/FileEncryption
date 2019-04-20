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
		
		updateMessage("Inizjalizacja...");
		updateProgress(0,100);
		
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
			File decFile = new File("./decrypted/", file.getName());
			decFile.createNewFile();
			long fileSize = file.length() - jsonSize;
			long decrypted = 0;
			
			try (OutputStream outputStream = new BufferedOutputStream(
					new CipherOutputStream(new FileOutputStream(decFile), blowfish.getCipher()))) {
				
				updateMessage("Deszyfracja");

				while ((readSize = bufferedInputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, readSize);
					decrypted += readSize;
					updateProgress(decrypted, fileSize);
				}

				System.out.println("Client decrypted file");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			updateMessage("Gotowe");
			updateProgress(100,100);
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
