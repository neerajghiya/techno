import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mbusa.mef.util.FileUtil;
import com.mbusa.mef.util.XmlUtil;

public class XMLEncryptionWithCert {
	static org.apache.commons.logging.Log log =
	        org.apache.commons.logging.LogFactory.getLog(
	        		XMLEncryptionWithCert.class.getName());
	
	static {
        org.apache.xml.security.Init.init();
    }
	
	 public static PublicKey getPublicKey(String key) throws IOException, GeneralSecurityException {
	        
	      //  System.out.println("publicKeyPEM == " + publicKeyPEM );
	        FileInputStream fin = new FileInputStream(key);
	        CertificateFactory f = CertificateFactory.getInstance("X.509");
	        X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
	        PublicKey pk = certificate.getPublicKey();
	        return pk;
	        
	    }

    /**
     * Encryption method using symmetric keys
     * @param data Node, containing the data that requires encryption
     * @param skJceAlgorithmName Name of the data encryption symmetric key algorithm. e.g. 'AES'.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @return EncryptedData element wrapped within a container that also contains the unencrypted key encryption key.
     */
    public static Node encryptXMLSym(String data, String skJceAlgorithmName, String kekJceAlgorithmName, PublicKey publicKey) throws Exception
    {
    	Document doc = XmlUtil.parseAsXml(data);//new XDocument(data).getDocument();

        // Generate the symmetric key that will be used to encrypt the data
        //String skJceAlgorithmName = "AES";
        KeyGenerator skGenerator = KeyGenerator.getInstance(skJceAlgorithmName);
        skGenerator.init(128);
        Key sk = skGenerator.generateKey();

        // Encrypt the symmetric key
        String algorithmURI = XMLCipher.RSA_v1dot5;
        XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
        keyCipher.init(XMLCipher.WRAP_MODE, publicKey);
        EncryptedKey encryptedKey = keyCipher.encryptKey(doc, sk);
        encryptedKey.setRecipient("name:XB62DataPower");
        KeyInfo paramKeyInfo = new KeyInfo(doc);
        KeyName keyName = new KeyName(doc, "XB62DataPower");
        paramKeyInfo.add(keyName);
        encryptedKey.setKeyInfo(paramKeyInfo);
       // System.out.println(encryptedKey.getCipherData().getCipherValue().getValue());
        // Encrypt the data
        algorithmURI = XMLCipher.AES_128;
        XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);

        xmlCipher.init(XMLCipher.ENCRYPT_MODE, sk);

        // Include the KeyInfo within the encrypted data
        EncryptedData encryptedData = xmlCipher.getEncryptedData();
        KeyInfo keyInfo = new KeyInfo(doc);
        keyInfo.add(encryptedKey);
        encryptedData.setKeyInfo(keyInfo);
        
        // Replace the original data with the EncryptedData element.
        // 'false' indicates the root element should be included within the encrypted data.
        xmlCipher.doFinal(doc, doc.getDocumentElement(), false);
        
        doc.normalizeDocument();
        
        return doc.getDocumentElement();
    }

    
    
    public static void main(String[] args) throws Exception {
    	PublicKey publicKey = getPublicKey("D:\\MBFS_Encryption\\XB62DataPower-sscert.pem");
    	//PrivateKey privateKey = getPrivateKey("D:\\MBFS_Encryption\\XB62DataPower-privkey.pem");
    	
    	String message = FileUtil.readFile("D:\\MBFS_Encryption\\SampleMessage\\Request-2.txt");
    	System.out.println("---------------------------------------------------");
    	System.out.println(message);
    	System.out.println("---------------------------------------------------");
    	
    	
        Node secret = encryptXMLSym(message, "AES", "DESede", publicKey);
        String encryptedData = XmlUtil.serializeAsString(secret,true);
        System.out.println(encryptedData);
      
	}

}
