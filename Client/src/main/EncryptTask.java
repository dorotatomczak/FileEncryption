package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

import com.google.gson.Gson;

import javafx.concurrent.Task;
import main.util.LoggedInUser;
import main.util.RSAKeysUtils;

public class EncryptTask extends Task<Void> {

	private static final int port = 1234;
	private File file;
	private String mode;

	public EncryptTask(File file, String mode) {
		this.file = file;
		this.mode = mode;
	}

	@Override
	protected Void call() throws Exception {

		// narazie wysy³am do zalogowanego uzytk a nie wybranego z listy
		PublicKey key = RSAKeysUtils.loadPublicKey(LoggedInUser.loggedInUser.getLogin());
		String keyString = RSAKeysUtils.publicKeyToString(key);

		EncryptionDetails details = new EncryptionDetails(this.mode, keyString, file.getName(), file.length());
		System.out.println(String.valueOf(file.length()));
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

		// send file to encrypt
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int readSize;
			while ((readSize = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readSize);
			}
			System.out.println("Client sent file to encrypt");
		}
	}

	private void receive(DataInputStream ois) throws IOException {

		byte[] buffer = new byte[4096];
		int readSize;

		File file = new File("./encrypted/", ois.readUTF());

		file.createNewFile();

		try (FileOutputStream fos = new FileOutputStream(file)) {
			while ((readSize = ois.read(buffer)) != -1) {
				fos.write(buffer, 0, readSize);
			}
			System.out.println("Client received encrypted file");
		}
	}
}
