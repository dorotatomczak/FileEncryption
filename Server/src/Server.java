import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.Gson;

public class Server {

	static final int PORT = 1234;

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {

			while (true) {
				try (Socket socket = serverSocket.accept();
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						DataInputStream ois = new DataInputStream(socket.getInputStream())) {

					System.out.println("Received a connection from " + socket);

					String fileName = receiveAndEncrypt(ois);
					sendEncrypted(out, fileName);
				}catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private static void sendEncrypted(DataOutputStream out, String fileName) throws IOException {

		out.writeUTF(fileName);

		File file = new File("./encrypted/", fileName);

		// send encrypted file
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int readSize;
			while ((readSize = fis.read(buffer)) != -1) {
				out.write(buffer, 0, readSize);
			}
			System.out.println("Server sent encryptd file");
		}
	}

	private static String receiveAndEncrypt(DataInputStream ois) throws IOException, ClassNotFoundException,
			InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {

		// pobranie informacji o szyfrowaniu
		Gson gson = new Gson();
		EncryptionDetails eDetails = gson.fromJson(new StringReader(ois.readUTF()), EncryptionDetails.class);

		String fileName = eDetails.getFileName();
		PublicKey pubKey = publicKeyFromString(eDetails.getRsaPublicKey());
		String mode = eDetails.getMode();
		long fileSize = eDetails.getFileSize();

		Blowfish blowfish = new Blowfish(mode);

		File file = new File("./encrypted/", fileName);
		file.createNewFile();

		// dodanie informacji o deszyfrowaniu do pliku
		DecryptionDetails dDetails = new DecryptionDetails(mode, blowfish.decryptKey(pubKey));
		String jsonDDetails = gson.toJson(dDetails);
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(jsonDDetails);
			fw.write(System.lineSeparator());
		}

		byte[] buffer = new byte[4096];
		long totalRead=0;
		int readSize;

		// odbierz plik od klienta, zaszyfruj go i zapisz na dysku
		try (OutputStream outputStream = new BufferedOutputStream(
				new CipherOutputStream(new FileOutputStream(file, true), blowfish.getCipher()))) {
				
			while (fileSize > 0 && (readSize = ois.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
			{
			  outputStream.write(buffer,0,readSize);
			  fileSize -= readSize;
			}
			
			System.out.println("Server received and saved file");
		}

		return fileName;

	}

	private static PublicKey publicKeyFromString(String pub)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] bytes = Base64.getDecoder().decode(pub);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pubKey = kf.generatePublic(ks);
		return pubKey;
	}

}
