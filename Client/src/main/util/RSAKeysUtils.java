package main.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import main.database.User;

public class RSAKeysUtils {

	private static final int KEY_SIZE = 2048;
	private static final String PATH_PUB = "/keys/pub/";
	private static final String PATH_PVT = "/keys/pvt/";
	private static final int VECTOR_SIZE = 8;

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	// Converts byte array to hex string
	// From:
	// http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void generateRSAKeys(User user) {

		String login = user.getLogin();

		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(KEY_SIZE);
			KeyPair kp = kpg.generateKeyPair();
			PublicKey pub = kp.getPublic();
			PrivateKey pvt = kp.getPrivate();

			System.out.println("wygenerowany klucz prywatny: " + bytesToHex(pvt.getEncoded()));

			String workingDir = System.getProperty("user.dir");
			String pubPath = workingDir + PATH_PUB;
			String pvtPath = workingDir + PATH_PVT;

			saveToFile(pubPath + login + ".pub", pub.getEncoded());

			byte[] encodedPvtKey = encryptPrivateKey(user, pvt);

			saveToFile(pvtPath + login, encodedPvtKey);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveToFile(String path, byte[] key) {

		try {
			File file = new File(path);
			System.out.println(path);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(key);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] decrypt(User user, String data) throws Exception{

		PrivateKey pvt;
		pvt = decryptPrivateKey(user);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pvt);
		byte[] sessionKey = cipher.doFinal(Base64.getDecoder().decode(data));
		return sessionKey;
	}
	
	public static byte[] decrypt(User user, byte[] data) throws Exception {
		PrivateKey pvt;
		pvt = decryptPrivateKey(user);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pvt);
		byte[] sessionKey = cipher.doFinal(data);
		return sessionKey;
	}

	public static PrivateKey loadPrivateKey(byte[] encodedKey) throws Exception {
		/* Generate private key. */
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(encodedKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pvt = kf.generatePrivate(ks);

		System.out.println("odszyfrowany klucz prywatny: " + bytesToHex(pvt.getEncoded()));

		return pvt;
	}

	public static PublicKey loadPublicKey(String login) throws Exception {
		/* Read all the public key bytes */
		String workingDir = System.getProperty("user.dir");
		Path path = Paths.get(workingDir + PATH_PUB + login + ".pub");
		byte[] bytes = Files.readAllBytes(path);

		/* Generate public key. */
		X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pub = kf.generatePublic(ks);
		return pub;
	}

	public static String publicKeyToString(PublicKey pub) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec ks = kf.getKeySpec(pub, X509EncodedKeySpec.class);
		return Base64.getEncoder().encodeToString(ks.getEncoded());
	}

	// Blowfish, CBC, password (funkcja skr�tu: PBKDF2WithHmacSHA512)
	private static byte[] encryptPrivateKey(User user, PrivateKey pvt) throws Exception {

		// get key
		byte[] key = user.getPassword().getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		// generate Initialization Vector
		byte[] vector = new byte[VECTOR_SIZE];
		SecureRandom srandom = new SecureRandom();
		srandom.nextBytes(vector);
		IvParameterSpec ivSpec = new IvParameterSpec(vector);

		System.out.println("wygenerowany wektor: " + bytesToHex(vector));

		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] encoding = cipher.doFinal(pvt.getEncoded());

		System.out.println("zaszyfrowany klucz prywatny: " + bytesToHex(encoding));

		// concatenate vector array and encoding array
		byte[] vectorAndEncodedKey = new byte[vector.length + encoding.length];
		System.arraycopy(vector, 0, vectorAndEncodedKey, 0, vector.length);
		System.arraycopy(encoding, 0, vectorAndEncodedKey, vector.length, encoding.length);

		return vectorAndEncodedKey;
	}

	public static PrivateKey decryptPrivateKey(User user) throws Exception {
		String workingDir = System.getProperty("user.dir");
		String path = workingDir + PATH_PVT + user.getLogin();

		// get key
		byte[] key = user.getPassword().getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

		// get vector and private key
		byte[] vectorAndEncodedKey = loadFromFile(path);
		byte[] vector = new byte[VECTOR_SIZE];
		byte[] encodedKey = new byte[vectorAndEncodedKey.length - VECTOR_SIZE];

		// retrieve vector and encoded key from array
		System.arraycopy(vectorAndEncodedKey, 0, vector, 0, vector.length);
		System.arraycopy(vectorAndEncodedKey, vector.length, encodedKey, 0, encodedKey.length);
		IvParameterSpec ivSpec = new IvParameterSpec(vector);

		System.out.println("odczytany wektor: " + bytesToHex(vector));
		System.out.println("odczytany zaszyfrowany klucz prywatny: " + bytesToHex(encodedKey));

		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] encoding = cipher.doFinal(encodedKey);

		return loadPrivateKey(encoding);
	}

	private static byte[] loadFromFile(String path) throws IOException {

		byte[] bytes = Files.readAllBytes(Paths.get(path));
		return bytes;
	}
}
