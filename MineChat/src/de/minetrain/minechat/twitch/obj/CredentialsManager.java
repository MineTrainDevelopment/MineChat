package de.minetrain.minechat.twitch.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.directory.InvalidAttributesException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialsManager {
	private static final Logger logger = LoggerFactory.getLogger(CredentialsManager.class);
	protected static String SECRET_KEY;
	protected static  String SALT;
	protected static final String INIT_VECTOR = "jhrasrtzjew485wd";
	protected static final String algorithm = "PBKDF2WithHmacSHA256";
	protected static final String placeHolder = "Jä7bAtPübAt";
	protected static final String fileLocation = "data/data.minefile";

	protected static String clientId;
	protected static String clientSecret;
	protected static String oAuth2Token;

	public CredentialsManager() throws InvalidAttributesException, Exception {
	    try {
	    	if(!new File(fileLocation).exists()){
				throw new InvalidAttributesException(fileLocation+" not found.");
	    	}
	    	
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileLocation));
			String[] split = bufferedReader.readLine().split(placeHolder);
			bufferedReader.close();
			
			if(split.length != 11){
				deleteCredentialsFile();
				throw new InvalidAttributesException("Invalid credentials file.");
			}

			SECRET_KEY = split[5];
			SALT = split[7];
			clientId = decrypt(split[1]);
			clientSecret = decrypt(split[3]);
			oAuth2Token = decrypt(split[9]);
			
		} catch (InvalidAttributesException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Something went wrong while trying to encrypt credentials data.", ex);
			throw ex;
		}
	}

    public CredentialsManager(String clientID, String clientSecret, String OAuth2Token) throws Exception {
    	SECRET_KEY = generateBoilerplate();
    	SALT = generateBoilerplate();
		String credentials = generateBoilerplate()+placeHolder+encrypt(clientID)+placeHolder+generateBoilerplate()+placeHolder+encrypt(clientSecret)+placeHolder+generateBoilerplate()+placeHolder+SECRET_KEY+placeHolder+generateBoilerplate()+placeHolder+SALT+placeHolder+generateBoilerplate()+placeHolder+encrypt(OAuth2Token)+placeHolder+generateBoilerplate();
		try (PrintWriter writer = new PrintWriter(fileLocation)) {
			writer.println(credentials);
		}
	}
    
    public static void deleteCredentialsFile(){
    	new File(fileLocation).delete();
    }
    
    public static String encrypt(String input) throws Exception {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(INIT_VECTOR.getBytes()));

        byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public static String decrypt(String encryptedInput) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(INIT_VECTOR.getBytes()));

        byte[] decoded= Base64.getDecoder().decode(encryptedInput);
        byte[] decrypted = cipher.doFinal(decoded);
        
        return new String(decrypted, StandardCharsets.UTF_8).trim();
    }
    
    private static final String generateBoilerplate() {
        String alphabet = "/\\===*#ÄäÜüÖöabcdefghijklmnopqrstuvwABCDEFGHIKLMJNOPQRSTUVWXYZ1234567890";
        String output = "";
        for (int i = 0; i < Math.random()*250+25; i++) {
            int index = (int) (Math.random() * alphabet.length());
            output += alphabet.charAt(index);
        }
        
        return output;
    }
    
    /**
	 * @return The user's Twitch API client ID.
	 */
	public String getClientID() {
		return clientId;
	}
	
	/**
	 * @return The user's Twitch API client secret.
	 */
	public String getClientSecret() {
		return clientSecret;
	}
	
	public String getOAuth2Token() {
		return oAuth2Token;
	}
}
