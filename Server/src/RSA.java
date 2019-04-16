import java.security.PublicKey;
import javax.crypto.Cipher;

public class RSA {
	
	public static byte[] encrypt(PublicKey rsaPublicKey, byte[] data) throws Exception {
        Cipher rsa;
        rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        return rsa.doFinal(data);
	}
	
}
