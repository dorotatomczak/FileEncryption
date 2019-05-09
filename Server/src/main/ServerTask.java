package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.CipherOutputStream;

import com.google.gson.Gson;

import javafx.concurrent.Task;
import main.blowfish.Blowfish;
import main.blowfish.BlowfishBase;

public class ServerTask extends Task {

	static final int PORT = 1235;
	// TODO zmienic sciezke
	public static volatile File fileToEncrypt = new File(
			"../Server/main/resource/default.jpg");
	
	public ServerTask() {
		String workingDir = System.getProperty("user.dir");
		fileToEncrypt = new File(workingDir+"/src/main/resource/default.jpg");
	}

	@Override
	protected Object call() throws Exception {
		
		updateMessage("Serwer wystartowal");

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {

			while (true) {
				updateMessage("Serwer nasluchuje na porcie "+PORT);
				
				try (Socket socket = serverSocket.accept();
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						DataInputStream ois = new DataInputStream(socket.getInputStream())) {

					updateMessage("Serwer otrzymal polaczenie od "+socket);

					String fileName = receiveAndEncrypt(ois);
					sendEncrypted(out, fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendEncrypted(DataOutputStream out, String fileName) throws IOException {

		out.writeUTF(fileName);

		File file = new File(".", "tmp");

		// send file size to enable client to track progress
		out.writeLong(file.length());

		// send encrypted file
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int readSize;
			updateMessage("Wysy³anie zaszyfrowanego pliku do klienta");
			while ((readSize = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readSize);
			}
			updateMessage("Zakoñczenie wysy³ania pliku");
		}

		// delete file
		file.delete();
	}

	private String receiveAndEncrypt(DataInputStream ois) throws Exception {

		// pobranie informacji o szyfrowaniu
		Gson gson = new Gson();
		EncryptionDetails eDetails = gson.fromJson(new StringReader(ois.readUTF()), EncryptionDetails.class);
		
		updateMessage("Serwer odebra³ dane o enkrypcji od klienta");

		BlowfishBase blowfish = BlowfishBase.getBlowfish(eDetails.getMode());

		String fileName = eDetails.getFileName() + "." + getFileExtension(fileToEncrypt.getName());
		System.out.println(fileName);

		File file = new File(".", "tmp");
		file.createNewFile();

		// dodanie informacji potrzebnych do deszyfracji pliku
		DecryptionDetails dDetails = createDecryptionDetails(eDetails, blowfish);
		String jsonDDetails = gson.toJson(dDetails);

		int jsonSize = (int) jsonDDetails.getBytes().length;

		try (FileOutputStream fos = new FileOutputStream(file);) {
			fos.write(intToByteArray(jsonSize));
			fos.write(jsonDDetails.getBytes());
		}

		byte[] buffer = new byte[4096];
		int readSize;

		// wczytaj plik, zaszyfruj go i zapisz na dysku
		try (FileInputStream fileInputStream = new FileInputStream(fileToEncrypt);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				OutputStream outputStream = new BufferedOutputStream(
						new CipherOutputStream(new FileOutputStream(file, true), blowfish.getCipher()))) {
			
			updateMessage("Szyfrowanie pliku");

			while ((readSize = bufferedInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readSize);
			}

			System.out.println("Zakoñczono szyfrowanie pliku");
		}

		return fileName;

	}

	private PublicKey publicKeyFromString(String pub) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(pub);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pubKey = kf.generatePublic(ks);
		return pubKey;
	}

	private byte[] intToByteArray(int data) {

		byte[] result = new byte[4];

		result[0] = (byte) ((data & 0xFF000000) >> 24);
		result[1] = (byte) ((data & 0x00FF0000) >> 16);
		result[2] = (byte) ((data & 0x0000FF00) >> 8);
		result[3] = (byte) ((data & 0x000000FF) >> 0);

		return result;
	}

	private String getFileExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	private DecryptionDetails createDecryptionDetails(EncryptionDetails ed, BlowfishBase blowfish) throws Exception {

		List<Receiver> receiversED = ed.getReceivers();
		List<Receiver> receiversDD = new ArrayList<>();

		for (Receiver receiverED : receiversED) {
			PublicKey pubKey = publicKeyFromString(receiverED.getKey());
			Receiver receiverDD = new Receiver(receiverED.getLogin(), blowfish.encryptKey(pubKey));
			receiversDD.add(receiverDD);
		}

		String mode = ed.getMode().split("/")[1];
		switch (mode) {
		case "ECB":
			return new DecryptionDetails(ed.getMode(), receiversDD);
		default:
			String vector = ((Blowfish) blowfish).getVector();
			return new DecryptionDetails(ed.getMode(), receiversDD, vector);
		}
	}

}
