package de.minetrain.minechat.twitch.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Arrays;
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
	protected static String SALT;
	protected static String INIT_VECTOR;// = "jhrasrtzjew485wd";
	protected static String algorithm;// = "PBKDF2WithHmacSHA256";
	protected static String placeHolder;// = "Jä7bAtPübAt";
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
			String fileContent = bufferedReader.readLine();
			placeHolder = fileContent.substring(0, 10);
			
			String[] split = fileContent.split(placeHolder);
			bufferedReader.close();
			
			if(split.length != 16){
				deleteCredentialsFile();
				throw new InvalidAttributesException("Invalid credentials file.");
			}


			INIT_VECTOR = split[2];
			algorithm = split[4];
			SECRET_KEY = split[6];
			SALT = split[8];
			clientId = decrypt(split[10]);
			clientSecret = decrypt(split[12]);
			oAuth2Token = decrypt(split[14]);
			
		} catch (InvalidAttributesException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Something went wrong while trying to encrypt credentials data.", ex);
			throw ex;
		}
	}

    public CredentialsManager(String clientId, String clientSecret, String oAuth2Token) throws Exception {
    	SECRET_KEY = generateBoilerplate();
    	SALT = generateBoilerplate();
    	
    	INIT_VECTOR = generateSimpleBoilerplate().substring(0, 16);
    	algorithm =  "PBKDF2WithHmacSHA256";

		clientId = encrypt(clientId);
		clientSecret = encrypt(clientSecret);
		oAuth2Token = encrypt(oAuth2Token);
		System.out.println(encrypt("PferdchenIstFein"));
		
		CredentialsManager.clientId = clientId;
		CredentialsManager.clientSecret = clientSecret;
		CredentialsManager.oAuth2Token = oAuth2Token;
		
		placeHolder = createPlaceHolder(SECRET_KEY + SALT + INIT_VECTOR + algorithm + clientId + clientSecret + oAuth2Token);
		
		StringBuilder credentials = new StringBuilder();
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //0
		credentials.append(INIT_VECTOR);									   //1
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //2
		credentials.append(algorithm);									   	   //3
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //4
		credentials.append(SECRET_KEY);									   	   //5
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //6
		credentials.append(SALT);									   		   //7
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //8
		credentials.append(clientId);									       //9
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //10
		credentials.append(clientSecret);									   //11
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //12
		credentials.append(oAuth2Token);									   //13
		credentials.append(placeHolder + generateBoilerplate() + placeHolder); //14

		
//		String credentials = generateBoilerplate() + placeHolder + encryptClientID + placeHolder
//				+ generateBoilerplate() + placeHolder + encryptClientSecret + placeHolder + generateBoilerplate()
//				+ placeHolder + SECRET_KEY + placeHolder + generateBoilerplate() + placeHolder + SALT + placeHolder
//				+ generateBoilerplate() + placeHolder + encryptOAuth2Token + placeHolder + generateBoilerplate();
		
		try (PrintWriter writer = new PrintWriter(fileLocation)) {
			writer.println(credentials.toString());
		}
	}
    
	private static final String createPlaceHolder(String values) {
		String placeHolder = generateSimpleBoilerplate().substring(0, 10);
		if(values.contains(placeHolder)){
			logger.debug("illegal placeHolder! Regenerating it...");
			return createPlaceHolder(values);
		}

		logger.debug("PlaceHolder gegenerated!");
		return placeHolder;
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
        String alphabet = "/\\===#ÄäÜüÖöabcdefghijklmnopqrstuvwABCDEFGHIKLMJNOPQRSTUVWXYZ1234567890";
        String output = "";
        for (int i = 0; i < Math.random()*250+25; i++) {
            int index = (int) (Math.random() * alphabet.length());
            output += alphabet.charAt(index);
        }
        
        return output;
    }
    
    private static final String generateSimpleBoilerplate() {
        String alphabet = "abcdefghijklmnopqrstuvwABCDEFGHIKLMJNOPQRSTUVWXYZ1234567890";
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
