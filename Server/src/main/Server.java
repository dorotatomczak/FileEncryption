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

import blowfish.BlowfishBase;
import blowfish.Blowfish;

public class Server {

	static final int PORT = 1235;
	private static final String FILE_TO_ENCRYPT_PATH = "C:\\Users\\Dorota\\Pictures\\Wallpapers\\maxresdefault.jpg";

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {

			while (true) {
				try (Socket socket = serverSocket.accept();
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						DataInputStream ois = new DataInputStream(socket.getInputStream())) {

					System.out.println("Received a connection from " + socket);

					String fileName = receiveAndEncrypt(ois);
					sendEncrypted(out, fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendEncrypted(DataOutputStream out, String fileName) throws IOException {

		out.writeUTF(fileName);

		File file = new File(".", "tmp");
		
		// send file size to enable client to track progress
		out.writeLong(file.length());

		// send encrypted file
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int readSize;
			while ((readSize = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readSize);
			}
			System.out.println("Server sent encrypted file");
		}

		// delete file
		file.delete();
	}

	private static String receiveAndEncrypt(DataInputStream ois) throws Exception {

		// pobranie informacji o szyfrowaniu
		Gson gson = new Gson();
		EncryptionDetails eDetails = gson.fromJson(new StringReader(ois.readUTF()), EncryptionDetails.class);

		BlowfishBase blowfish = BlowfishBase.getBlowfish(eDetails.getMode());

		File fileToEncrypt = new File(FILE_TO_ENCRYPT_PATH);
		String fileName = eDetails.getFileName() + "."+ getFileExtension(fileToEncrypt.getName());
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

			while ((readSize = bufferedInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readSize);
			}

			System.out.println("Server encrypted and saved file");
		}

		return fileName;

	}

	private static PublicKey publicKeyFromString(String pub) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(pub);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pubKey = kf.generatePublic(ks);
		return pubKey;
	}

	private static byte[] intToByteArray(int data) {

		byte[] result = new byte[4];

		result[0] = (byte) ((data & 0xFF000000) >> 24);
		result[1] = (byte) ((data & 0x00FF0000) >> 16);
		result[2] = (byte) ((data & 0x0000FF00) >> 8);
		result[3] = (byte) ((data & 0x000000FF) >> 0);

		return result;
	}

	private static String getFileExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}
	
	private static DecryptionDetails createDecryptionDetails(EncryptionDetails ed, BlowfishBase blowfish) throws Exception {
		
		List<Receiver> receiversED = ed.getReceivers();
		List<Receiver> receiversDD = new ArrayList<>();
	
		for (Receiver receiverED : receiversED) {
			PublicKey pubKey = publicKeyFromString(receiverED.getKey());
			Receiver receiverDD =  new Receiver(receiverED.getLogin(), blowfish.encryptKey(pubKey));
			receiversDD.add(receiverDD);
		}

		String mode = ed.getMode().split("/")[1];
		switch (mode) {
		case "ECB":
			return new DecryptionDetails(ed.getMode(),receiversDD);
		default:
			String vector = ((Blowfish) blowfish).getVector();
			return new DecryptionDetails(ed.getMode(),receiversDD, vector);
		}
	}


}
