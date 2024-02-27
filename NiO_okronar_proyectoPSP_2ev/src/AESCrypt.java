import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
	private static SecretKey generateSecretKey() throws Exception {
	    // Utilizamos una clave de 16 bytes (128 bits) para AES
	    String keyString = "your_secret_key";
	    byte[] keyBytes = Arrays.copyOf(keyString.getBytes(StandardCharsets.UTF_8), 16);
	    return new SecretKeySpec(keyBytes, "AES");
	}

	public static String encrypt(String message) {
	    try {
	        SecretKey secretKey = generateSecretKey();
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
	        return Base64.getEncoder().encodeToString(encryptedBytes);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public static String decrypt(String encryptedMessage) {
	    try {
	        SecretKey secretKey = generateSecretKey();
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
	        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
	        return new String(decryptedBytes, StandardCharsets.UTF_8);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	public static void saveToFile(String content, String fileName) {
		try {
            Path path = Paths.get(fileName);
            Files.write(path, content.getBytes());
            System.out.println("Mensaje encriptado guardado en el archivo: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    
    public static String loadFromFile(String fileName) {
    	try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path);
            if (!lines.isEmpty()) {
                String encryptedContent = lines.get(0);
                System.out.println("Contenido del archivo: " + encryptedContent);
                return decrypt(encryptedContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
