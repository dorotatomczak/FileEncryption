import java.io.BufferedOutputStream;
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
				final Socket socket = serverSocket.accept();
				System.out.println("Received a connection from " + socket);

				String fileName = receiveAndEncrypt(socket);
				sendEncrypted(socket, fileName);

				socket.close();
			}

		} catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private static void sendEncrypted(Socket socket, String fileName) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream (socket.getOutputStream())){
			
			out.writeUTF(fileName);
			
			File file = new File("./encrypted/", fileName);
			
			//send encrypted file
			try (FileInputStream fis = new FileInputStream(file)){
                byte[] buffer = new byte[4096];
                int readSize;
                while ((readSize = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, readSize);
                }  
			}
		}
	}

	private static String receiveAndEncrypt(Socket socket) throws IOException, ClassNotFoundException,
			InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {

		try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

			// pobranie informacji o szyfrowaniu
			Gson gson = new Gson();
			EncryptionDetails eDetails = gson.fromJson(new StringReader(ois.readUTF()), EncryptionDetails.class);

			String fileName = eDetails.getFileName();
			PublicKey pubKey = publicKeyFromString(eDetails.getRsaPublicKey());
			String mode = eDetails.getMode();

			Blowfish blowfish = new Blowfish(mode);

			File file = new File("./encrypted/", fileName);
			file.createNewFile();

			// dodanie informacji o deszyfrowaniu do pliku
			DecryptionDetails dDetails = new DecryptionDetails(mode, blowfish.decryptKey(pubKey));
			String jsonDDetails = gson.toJson(dDetails);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jsonDDetails);
			}

			byte[] buffer = new byte[4096];
			int readSize;

			// odbierz plik od klienta, zaszyfruj go i zapisz na dysku
			try (OutputStream outputStream = new BufferedOutputStream(
					new CipherOutputStream(new FileOutputStream(file, true), blowfish.getCipher()))) {
				while ((readSize = ois.read(buffer)) != -1) {
					outputStream.write(buffer, 0, readSize);
				}

			}
			
			return fileName;
		}
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
