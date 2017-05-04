import java.security.Key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.Cipher;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.Constants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//Hyfinity classes used as helpers for DOM and XPath manipulation. Can be replaced with suitable W3C Document operations if required.
import com.hyfinity.utils.xml.XDocument;

import org.apache.commons.codec.binary.Base64;

public class SampleEncryption {

    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Encryption method using symmetric keys
     * @param data Node, containing the data that requires encryption
     * @param skJceAlgorithmName Name of the data encryption symmetric key algorithm. e.g. 'AES'.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @return EncryptedData element wrapped within a container that also contains the unencrypted key encryption key.
     */
    public static Node encryptXMLSym(Node data, String skJceAlgorithmName, String kekJceAlgorithmName) throws Exception
    {
    	Document doc = new XDocument(data).getDocument();

        // Generate the symmetric key that will be used to encrypt the data
        //String skJceAlgorithmName = "AES";
        KeyGenerator skGenerator = KeyGenerator.getInstance(skJceAlgorithmName);
        skGenerator.init(128);
        Key sk = skGenerator.generateKey();

        // Generate the key that will be used to encrypt the symmetric key
        //String kekJceAlgorithmName = "DESede";
        KeyGenerator kekGenerator = KeyGenerator.getInstance(kekJceAlgorithmName);
        Key kek = kekGenerator.generateKey();

        // Encrypt the symmetric key
        String algorithmURI = XMLCipher.TRIPLEDES_KeyWrap;
        XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
        keyCipher.init(XMLCipher.WRAP_MODE, kek);
        EncryptedKey encryptedKey = keyCipher.encryptKey(doc, sk);

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

        Document response = new XDocument("<EncryptionWrapper><KeyEncryptionKey encoding='Base64' desc='***UNENCRYPTED*** symmetric secret key used to encrypt the data encryption key'>"
            + new String(Base64.encodeBase64(kek.getEncoded()))
            + "</KeyEncryptionKey></EncryptionWrapper>").getDocument();
        response.getDocumentElement().appendChild(response.importNode(doc.getDocumentElement(), true));
        return response.getDocumentElement();
    }

    /**
     * Decryption method using symmetric keys
     * @param data Node, containing the EncryptedData element that requires decrypting
     * @param kekString The Base64 encoded and unencrypted secret key encryption key that was used during encryption.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @return The original decrypted XML node.
     */
    public static Node decryptXMLSym(Node data, String kekString, String kekJceAlgorithmName) throws Exception
    {
        Document document = new XDocument(data).getDocument();

        // Locate the EncryptedData element
        Element encryptedDataElement = (Element)document.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

        // Identify the key that will be used to decrypt the data encryption key.
        // This is the Base64 encoded kekString param
        // The key used to decrypt the data will be obtained from the
        // KeyInfo element of the EncrypteData element using the kekString.

        //String kekJceAlgorithmName = "DESede";
        DESedeKeySpec keySpec = new DESedeKeySpec(Base64.decodeBase64(kekString.getBytes()));
        SecretKeyFactory skf = SecretKeyFactory.getInstance(kekJceAlgorithmName);
        SecretKey kek = skf.generateSecret(keySpec);

        // Decrypt the data
        String algorithmURI = XMLCipher.AES_128;
        XMLCipher xmlCipher = XMLCipher.getInstance();
        xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
        xmlCipher.setKEK(kek);

        // Replace the EncryptedData element with the decrypted fragment.
        xmlCipher.doFinal(document, encryptedDataElement);

        return document.getDocumentElement();
    }

    /**
     * Encryption method using symmetric keys. The final key encryption key in encrypted using a public key.
     * @param data Node, containing the data that requires encryption
     * @param ksType keystore type
     * @param ksFile location of the keystore file
     * @param ksPass keystore password
     * @param skJceAlgorithmName Name of the data encryption symmetric key algorithm. e.g. 'AES'.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @param certAlias certificate alias.
     * @return EncryptedData element wrapped within a container that also contains the public key encrypted key encryption key.
     */
    public static Node encryptXMLAsym(Node data,
            String ksType,
            String ksFile,
            String ksPass,
            String skJceAlgorithmName,
            String kekJceAlgorithmName,
            String certAlias) throws Exception
    {
        Document doc = new XDocument(data).getDocument();

        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(ksFile);
        ks.load(fis, ksPass.toCharArray());

        // Generate the key that will be used to encrypt the data
        //String skJceAlgorithmName = "AES";
        KeyGenerator skGenerator = KeyGenerator.getInstance(skJceAlgorithmName);
        skGenerator.init(128);
        Key sk = skGenerator.generateKey();

        // Generate the key that will be used to encrypt the data encryption key (sk)
        //String kekJceAlgorithmName = "DESede";
        KeyGenerator kekGenerator = KeyGenerator.getInstance(kekJceAlgorithmName);
        Key kek = kekGenerator.generateKey();

        // Obtain the public key that will be used to encrypt the key encryption key (kek)
        X509Certificate cert = (X509Certificate)ks.getCertificate(certAlias);
        Key publicKey = cert.getPublicKey();

        // Encrypt the key encryption key using the public key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKek = cipher.doFinal(kek.getEncoded());

        // Encrypt the data encryption key using the key encryption key
        String algorithmURI = XMLCipher.TRIPLEDES_KeyWrap;
        XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
        keyCipher.init(XMLCipher.WRAP_MODE, kek);
        EncryptedKey encryptedSk = keyCipher.encryptKey(doc, sk);

        // Encrypt the data using the data encryption key
        algorithmURI = XMLCipher.AES_128;
        XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, sk);

        // Include the data encryption KeyInfo within the encrypted data
        EncryptedData encryptedData = xmlCipher.getEncryptedData();
        KeyInfo keyInfo = new KeyInfo(doc);
        keyInfo.add(encryptedSk);
        encryptedData.setKeyInfo(keyInfo);

        // Replace the original data with the EncryptedData element.
        // 'false' indicates the root element should be included within the encrypted data.
        xmlCipher.doFinal(doc, doc.getDocumentElement(), false);

        Document response = new XDocument("<EncryptionWrapper><KeyEncryptionKey encoding='Base64' desc='Public key encrypted key encryption key'>"
            + new String(Base64.encodeBase64(encryptedKek))
            + "</KeyEncryptionKey></EncryptionWrapper>").getDocument();
        response.getDocumentElement().appendChild(response.importNode(doc.getDocumentElement(), true));

        return response.getDocumentElement();
    }

    /**
     * Decryption method using symmetric keys. The public key encrypted key encryption key is initially decrypted using the paired private key.
     * @param data Node, containing the data that requires decryption
     * @param kekString The Base64 encoded and public key encrypted secret key encryption key that was used during encryption.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @param ksType keystore type
     * @param ksFile location of the keystore file
     * @param ksPass keystore password
     * @param pkAlias private key alias
     * @param pkPass private key password
     * @return The original decrypted XML node.
     */
    public static Node decryptXMLAsym(Node data,
            String kekString,
            String kekJceAlgorithmName,
            String ksType,
            String ksFile,
            String ksPass,
            String pkAlias,
            String pkPass) throws Exception
    {
        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(ksFile);
        ks.load(fis, ksPass.toCharArray());

        // Locate the private key which is part of the pair for the public key that was used to encrypt the data encryption key.
        PrivateKey privateKey = (PrivateKey)ks.getKey(pkAlias, pkPass.toCharArray());

        Document document = new XDocument(data).getDocument();

        // Locate the EncryptedData element
        Element encryptedDataElement = (Element)document.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

        // Decrypt the key encryption key
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] kekBytes = cipher.doFinal(Base64.decodeBase64(kekString.getBytes()));
        DESedeKeySpec keySpec = new DESedeKeySpec(kekBytes);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(kekJceAlgorithmName);
        SecretKey kek = skf.generateSecret(keySpec);

        // Decrypt the data
        String algorithmURI = XMLCipher.AES_128;
        XMLCipher xmlCipher = XMLCipher.getInstance();
        xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
        xmlCipher.setKEK(kek);

        // Replace the EncryptedData element with the decrypted fragment.
        xmlCipher.doFinal(document, encryptedDataElement);

        return document.getDocumentElement();
    }

    /**
     * Basic RSA public key data encryption using a public key obtained from the given certificate info.
     * Encrypts an XML node.
     * @param data Node, containing the data that requires encryption
     * @param ksType keystore type
     * @param ksFile location of the keystore file
     * @param ksPass keystore password
     * @param certAlias certificate alias.
     * @return Base64 encoded string of the encrypted data node.
     */
    public static String encryptSimpleStrAsym(Node data,
            String ksType,
            String ksFile,
            String ksPass,
            String certAlias) throws Exception
    {
        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(ksFile);
        ks.load(fis, ksPass.toCharArray());

        // Extract the public key from the certificate
        X509Certificate cert = (X509Certificate)ks.getCertificate(certAlias);
        Key publicKey = cert.getPublicKey();

        // Encrypt data
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(new XDocument(data).toString().getBytes());

        return new String(Base64.encodeBase64(encryptedData));
    }

    /**
     * Basic RSA data decryption using a private key from the original public/private key pair.
     * @param data String, containing the data that requires decryption
     * @param ksType keystore type
     * @param ksFile location of the keystore file
     * @param ksPass keystore password
     * @param pkAlias private key alias
     * @param pkPass private key password
     * @return The original decrypted XML node.
     */
    public static Node decryptSimpleStrAsym(String data,
            String ksType,
            String ksFile,
            String ksPass,
            String pkAlias,
            String pkPass) throws Exception
    {
        KeyStore ks = KeyStore.getInstance(ksType);
        FileInputStream fis = new FileInputStream(ksFile);
        ks.load(fis, ksPass.toCharArray());

        // Locate private key for decryption.
        PrivateKey privateKey = (PrivateKey)ks.getKey(pkAlias, pkPass.toCharArray());

        // Decrypt data
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(Base64.decodeBase64(data.getBytes()));

        return new XDocument(new String(decryptedData)).getRootNode();
    }
}