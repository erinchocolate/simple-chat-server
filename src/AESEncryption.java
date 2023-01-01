
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
public class AESEncryption {
	private SecretKey key;
    private int KEY_SIZE = 128;
    private int T_LEN = 128;
    private byte[] IV;

    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    public void initFromStrings(String secretKey, String IV){
        key = new SecretKeySpec(decode(secretKey),"AES");
        this.IV = decode(IV);
    }
    
    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
   
    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    private String getKey() {
    	return encode(key.getEncoded());
    }
    
    private String getIV() {
    	return encode(IV);
    }

    public static void main(String[] args) {
        try {
            AESEncryption aes = new AESEncryption();
            aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==","e3IYYJC2hxe24/EO");
            String decryptedMessage = aes.decrypt("mqQQF6K2GEaR0JKTd1yN58Mbs7qeYamM0xgung==");
            System.err.println("Decrypted Message : " + decryptedMessage);
        } catch (Exception ignored) {
        }
    }
    
    
}
