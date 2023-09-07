
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/*  
 
Name: AES Class


Purpose: uses AES 128 bit encryption to encrypt and decrypt messages sent from clients 

Usage: When a client enters "encrypt" , this turns on encryption, and allows for all their messages to be encrypted when sent. 

Subroutines/libraries required:
java.io.UnsupportedEncodingException
java.security.MessageDigest
java.security.NoSuchAlgorithmException
javax.crypto.Cipher

Reference: https://howtodoinjava.com/security/java-aes-encryption-example/

*/

public class AES {
 
    private static SecretKeySpec secretKey;
    private static byte[] key;
 
    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();			
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
  
    
	/*  
	  Name:	encrypt
	  
	  Purpose: Encrypts a a string 
	  
	  Usage: Used to Encrypt a clients message before sending.
	  
	  Subroutines/libraries required:

	 */
    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
  
    
    
	/*  
	  Name:	decrypt
	  
	  Purpose: Decrypts a a string 
	  
	  Usage: Used to decrypt encrypted messages from clients.
	  
	  Subroutines/libraries required:

	 */
    public static String decrypt(String strToDecrypt, String secret) 
    {
		
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.getBytes("UTF-8"))));
        } 
        catch (Exception e) 
        {
            //System.out.println("Error while decrypting: " + e.toString());
			 //System.out.println(strToDecrypt);
        }
        return strToDecrypt;
    }
}