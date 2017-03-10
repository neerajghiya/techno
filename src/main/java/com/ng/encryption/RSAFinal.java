import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Ricardo Sequeira
 *
 */
public class RSAFinal {
	
	private static final String ALGORITHM = "RSA";

    private static String getKey(String filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    /**
     * Constructs a private key (RSA) from the given file
     * 
     * @param filename PEM Private Key
     * @return RSA Private Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
        String privateKeyPEM = getKey(filename);
        return getPrivateKeyFromString(privateKeyPEM);
    }

    /**
     * Constructs a private key (RSA) from the given string
     * 
     * @param key PEM Private Key
     * @return RSA Private Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        // Remove the first and last lines
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
       privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
       // System.out.println("privateKeyPEM == " + privateKeyPEM );
        byte[] publicBytes = Base64.decodeBase64(privateKeyPEM.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privKey = keyFactory.generatePrivate(keySpec);
        return privKey;
        
    }

    /**
     * Constructs a public key (RSA) from the given file
     * 
     * @param filename PEM Public Key
     * @return RSA Public Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static PublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
        String publicKeyPEM = getKey(filename);
        //System.out.println("getPublicKey == " + publicKeyPEM );
        return getPublicKeyFromString(publicKeyPEM);
    }

    /**
     * Constructs a public key (RSA) from the given string
     * 
     * @param key PEM Public Key
     * @return RSA Public Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static PublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;

        // Remove the first and last lines
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
      //  System.out.println("publicKeyPEM == " + publicKeyPEM );
        
        byte[] publicBytes = Base64.decodeBase64(publicKeyPEM.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
        
    }

    /**
     * Encrypts the text with the public key (RSA)
     * 
     * @param rawText Text to be encrypted
     * @param publicKey
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
    	/*PublicKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));*/
    	Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.PUBLIC_KEY, publicKey);
        
        return cipher.doFinal(rawText.getBytes());
    }

    /**
     * Decrypts the text with the private key (RSA)
     * 
     * @param cipherText Text to be decrypted
     * @param privateKey
     * @return Decrypted text (Base64 encoded)
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static byte[] decrypt(byte[] cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
    	/*PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));*/
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.PRIVATE_KEY, privateKey);

        return cipher.doFinal(cipherText);
    }
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
    	PublicKey publicKey = getPublicKey("D:\\MBFS_Encryption\\id_rsa.pub");
    	PrivateKey privateKey = getPrivateKey("D:\\MBFS_Encryption\\id_rsa");
        String message = "hello World11111111111111113333333444444445555555555556666677777777777999999999999999991 1111111111222222222222222222777777777777777777777777777777777777 vvvvvvvvvvvvvvvvvvvvvvvvvv  wwwwwwwwwwwwwwwwwwwwwwww   rrrrrrrrrrrrrrrrrrrrrrrrrrrrr";
    	/*String message = "<star:Acknowledge confirm=\"Always\"/>" +
      "<star:CreditContractResponse>" +
         "<star:Header>" +
            "<star:FinanceType/>" +
            "<star:FundingStatus>Ownership Transfer Success</star:FundingStatus>" +
         "</star:Header>" +
         "<star:FinanceCompany>" +
            "<star:PartyId>MB</star:PartyId>" +
         "</star:FinanceCompany>" +
         "<star:Dealer>" +
            "<star:PartyId>70369</star:PartyId>" +
         "</star:Dealer>" +
         "<star:Financing>" +
		"<star:AmountFinanced currency=\"USD\">0</star:AmountFinanced>" +
		"</star:Financing>" +
      "</star:CreditContractResponse>";*/
        byte[] secret = encrypt(message, publicKey);
        //System.out.println(new String(secret, "UTF8"));
        byte[] recovered_message = decrypt(secret, privateKey);
        System.out.println(new String(recovered_message));
	}
}
