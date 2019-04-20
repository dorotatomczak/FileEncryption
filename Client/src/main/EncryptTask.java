package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

import com.google.gson.Gson;

import javafx.concurrent.Task;
import main.database.User;
import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class EncryptTask extends Task<Void> {

	private static final int port = 1234;
	private String mode;
	private String fileName;
	private int blockSize;
	private List<User> receivers;

	public EncryptTask(String mode, String fileName, List<User> receivers) {
		this.mode = mode;
		this.fileName = fileName;
		this.receivers = receivers;
	}
	
	public EncryptTask(String mode, String fileName, int blockSize, List<User> receivers) {
		this.mode = mode;
		this.fileName = fileName;
		this.blockSize = blockSize;
		this.receivers = receivers;
	}

	@Override
	protected Void call() throws Exception {
		
		updateMessage("Inizjalizacja...");
		updateProgress(0,100);

		// narazie wysy³am do zalogowanego uzytk a nie wybranego z listy
		//TODO wysylanie do wybranych uzytkownikow
		PublicKey key = RSAKeysUtils.loadPublicKey(LoggedInUser.loggedInUser.getLogin());
		String keyString = RSAKeysUtils.publicKeyToString(key);

		EncryptionDetails details = new EncryptionDetails(this.mode, keyString, fileName);
		Gson gson = new Gson();
		String jsonDetails = gson.toJson(details);

		// u mnie na VM: 10.0.2.2
		// na windowsie zmienic na localhost
		try (Socket socket = new Socket("localhost", port); 
				DataOutputStream out =  new DataOutputStream(socket.getOutputStream());
				DataInputStream ois =  new DataInputStream(socket.getInputStream())) {

			send(out,jsonDetails);
			receive(ois);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void send(DataOutputStream out, String jsonDetails) throws IOException {

		// send data
		out.writeUTF(jsonDetails);

		System.out.println("Client sent encryption details");
	}

	private void receive(DataInputStream ois) throws IOException {

		byte[] buffer = new byte[4096];
		int readSize;

		File file = new File("./encrypted/", ois.readUTF());

		file.createNewFile();

		try (FileOutputStream fos = new FileOutputStream(file)) {
			
			updateMessage("Szyfrowanie");
			updateProgress(0,100);
			
			long fileSize = ois.readLong();
			long received = 0;
			
			while ((readSize = ois.read(buffer)) != -1) {
				fos.write(buffer, 0, readSize);
				received += readSize;
				updateProgress(received,fileSize);
			}
			System.out.println("Client received encrypted file");
			
			updateMessage("Gotowe");
			updateProgress(100,100);
		}
		
	}
}
