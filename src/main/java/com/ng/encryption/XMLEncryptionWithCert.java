import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.ssl.PKCS8Key;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        System.out.println(encryptedKey.getCipherData().getCipherValue().getValue());
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

        
        return doc.getDocumentElement();
    }

    /**
     * Decryption method using symmetric keys
     * @param data Node, containing the EncryptedData element that requires decrypting
     * @param kekString The Base64 encoded and unencrypted secret key encryption key that was used during encryption.
     * @param kekJceAlgorithmName Name of the key encryption key algorithm. e.g. 'DESede'.
     * @return The original decrypted XML node.
     */
    public static Node decryptXMLSym(String data, String kekString, String kekJceAlgorithmName, PrivateKey privateKey) throws Exception
    {
        Document document = XmlUtil.parseAsXml(data);//new XDocument(data).getDocument();

        // Locate the EncryptedData element
        /*Element encryptedDataElement = (Element)document.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                "xenc:EncryptedData").item(0);*/
        
        Element encryptedDataElement = (Element)document.getElementsByTagName(
                "xenc:EncryptedData").item(0);
        
        // Identify the key that will be used to decrypt the data encryption key.
        // This is the Base64 encoded kekString param
        // The key used to decrypt the data will be obtained from the
        // KeyInfo element of the EncrypteData element using the kekString.

        //String kekJceAlgorithmName = "DESede";
     //   DESedeKeySpec keySpec = new DESedeKeySpec(Base64.decodeBase64(kekString.getBytes()));
    //    SecretKeyFactory skf = SecretKeyFactory.getInstance(kekJceAlgorithmName);
      //  SecretKey kek = skf.generateSecret(keySpec);

        // Decrypt the data
      //  String algorithmURI = XMLCipher.AES_128;
        XMLCipher xmlCipher = XMLCipher.getInstance();
        xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
        xmlCipher.setKEK(privateKey);
        String decryptedData = XmlUtil.serializeAsString(encryptedDataElement,true);
        System.out.println(decryptedData);
        // Replace the EncryptedData element with the decrypted fragment.
        xmlCipher.doFinal(document, encryptedDataElement);

        return document.getDocumentElement();
    }
    
    /**
     * Constructs a private key (RSA) from the given file
     * 
     * @param key PEM Private Key
     * @return RSA Private Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static PrivateKey getPrivateKey(String key) throws IOException, GeneralSecurityException {
        
       FileInputStream in = new FileInputStream( key );
       char[] password = { 'm', 'b', 'u', 's', 'a', 'e', 'a', 'i' };
       PKCS8Key pkcs8 = new PKCS8Key( in, password );
       byte[] decrypted = pkcs8.getDecryptedBytes();
       PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( decrypted );
       PrivateKey pk = KeyFactory.getInstance( "RSA" ).generatePrivate( spec );
       return pk;
        
    }
    
    public static void main(String[] args) throws Exception {
    	PublicKey publicKey = getPublicKey("D:\\MBFS_Encryption\\XB62DataPower-sscert.pem");
    	PrivateKey privateKey = getPrivateKey("D:\\MBFS_Encryption\\XB62DataPower-privkey.pem");
    	//PrivateKey privateKey = getPrivateKey("D:\\MBFS_Encryption\\XB62DataPower-privkey.pem");
       // String message = "hello World11111111111111113333333444444445555555555556666677777777777999999999999999991 1111111111222222222222222222777777777777777777777777777777777777 vvvvvvvvvvvvvvvvvvvvvvvvvv  wwwwwwwwwwwwwwwwwwwwwwww   rrrrrrrrrrrrrrrrrrrrrrrrrrrrr";
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
    	
    	String message = "<star:DataArea><star:Process acknowledge=\"Always\" confirm=\"Always\"/>" +
            "<star:CreditContract>" +
               "<star:Header>" +
                 "<star:DocumentDateTime> +2016-11-30T15:39:46-05:00</star:DocumentDateTime>" +
                  "<star:DocumentId>32000000219720</star:DocumentId>" +
                  "<star:DocumentVersionNumber>44</star:DocumentVersionNumber>" +
                  "<star:ContractFormNumber>EF-001-5086</star:ContractFormNumber>" +
                  "<star:FinanceType>L</star:FinanceType>" +
                  "<star:ApplicationType>I</star:ApplicationType>" +
                  "<star:ApplicationNumber>1611301226055</star:ApplicationNumber>" +
                  "<star:ContractExecutionState>FL</star:ContractExecutionState>" +
                  "<star:ContractNegotiationLanguage>en-US</star:ContractNegotiationLanguage>" +
                  "<star:ProductType>S</star:ProductType>" +
                  "<star:ContractFormRevisionDate>2016-10-01</star:ContractFormRevisionDate>" +
                  "<star:DealId>707</star:DealId>" +
               "</star:Header>" +
               "<star:FinanceCompany>" +
                  "<star:PartyId>MF</star:PartyId>" +
                  "<star:AlternatePartyIds>" +
                     "<star:Id>arpitad</star:Id>" +
                     "<star:AssigningPartyId>Other</star:AssigningPartyId>" +
                  "</star:AlternatePartyIds>" +
                  "<star:Name>Daimler Financial Services Americas LLC</star:Name>" +
                  "<star:Address>" +
                     "<star:AddressLine>PO Box 685</star:AddressLine>" +
                     "<star:City>Roanoke</star:City>" +
                     "<star:StateOrProvince>TX</star:StateOrProvince>" +
                     "<star:Country>US</star:Country>" +
                     "<star:PostalCode>76262</star:PostalCode>" +
                  "</star:Address>" +
                  "<star:CompanyCode>01</star:CompanyCode>" +
               "</star:FinanceCompany>" +
               "<star:Dealer>" +
                  "<star:PartyId>70369</star:PartyId>" +
                  "<star:DealerName>MB OF CORAL GABLES dba B.U.M. INC.</star:DealerName>" +
                  "<star:Address>" +
                     "<star:AddressLine>300 ALMERIA AVE</star:AddressLine>" +
                     "<star:City>CORAL GABLES</star:City>" +
                     "<star:County>MIAMI-DADE</star:County>" +
                     "<star:StateOrProvince>FL</star:StateOrProvince>" +
                     "<star:Country>US</star:Country>" +
                     "<star:PostalCode>34205</star:PostalCode>" +
                  "</star:Address>" +
                  "<star:Contact>" +
                     "<star:Telephone desc=\"Day Phone\">3054458593</star:Telephone>" +
                  "</star:Contact>" +
                  "<star:PreferredLanguage>en-US</star:PreferredLanguage>" +
                  "<star:DBAName>MB OF CORAL GABLES dba B.U.M. INC.</star:DBAName>" +
               "</star:Dealer>" +
               "<star:IndividualApplicant>" +
                  "<star:PartyId>493</star:PartyId>" +
                  "<star:AlternatePartyIds>" +
                     "<star:Id>111111111</star:Id>" +
                     "<star:AssigningPartyId>LegalId</star:AssigningPartyId>" +
                  "</star:AlternatePartyIds>" +
                  "<star:PersonName>" +
                     "<star:GivenName>KATELYN</star:GivenName>" +
                     "<star:FamilyName>BOARD</star:FamilyName>" +
                  "</star:PersonName>" +
                  "<star:Address qualifier=\"Billing\">" +
                     "<star:AddressLine>7 nw 127TH CT</star:AddressLine>" +
                     "<star:City>MIAMI</star:City>" +
                     "<star:County>MIAMI-DADE</star:County>" +
                     "<star:StateOrProvince>FL</star:StateOrProvince>" +
                     "<star:Country>US</star:Country>" +
                     "<star:PostalCode>33182</star:PostalCode>" +
                  "</star:Address>" +
                  "<star:Address qualifier=\"Garage\">" +
                     "<star:AddressLine>7 nw 127TH CT</star:AddressLine>" +
                     "<star:City>MIAMI</star:City>" +
                     "<star:County>MIAMI-DADE</star:County>" +
                     "<star:StateOrProvince>FL</star:StateOrProvince>" +
                     "<star:Country>US</star:Country>" +
                     "<star:PostalCode>33182</star:PostalCode>" +
                  "</star:Address>" +
                  "<star:Contact>" +
                     "<star:Telephone desc=\"Evening Phone\">3055429221</star:Telephone>" +
                  "</star:Contact>" +
                  "<star:Demographics>" +
                     "<star:BirthDate>1970-01-01</star:BirthDate>" +
                  "</star:Demographics>" +
               "</star:IndividualApplicant>" +
               "<star:CreditVehicle>" +
                  "<star:Model>GTS</star:Model>" +
                  "<star:ModelYear>2016</star:ModelYear>" +
                  "<star:ModelDescription>GTS</star:ModelDescription>" +
                  "<star:Make>MERCEDES</star:Make>" +
                  "<star:SaleClass>New</star:SaleClass>" +
                  "<star:CertifiedPreownedInd>0</star:CertifiedPreownedInd>" +
                  "<star:VIN>WDDYJ7JA4GA007544</star:VIN>" +
                  "<star:DeliveryMileage uom=\"M\">56</star:DeliveryMileage>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">136300</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Invoice</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">153625.00</star:VehiclePrice>" +
                     "<star:VehiclePricingType>MSRP</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">153625.00</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Base MSRP</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">153625.00</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Final MSRP</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">148369.89</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Selling Price</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">151526.09</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Net Cap Cost</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:Pricing>" +
                     "<star:VehiclePrice currency=\"USD\">153068.89</star:VehiclePrice>" +
                     "<star:VehiclePricingType>Gross Cap Cost</star:VehiclePricingType>" +
                  "</star:Pricing>" +
                  "<star:CollateralType>1</star:CollateralType>" +
                  "<star:VehicleUse>P</star:VehicleUse>" +
                  "<star:TotalOptionsAmount currency=\"USD\">0</star:TotalOptionsAmount>" +
                  "<star:VehicleOwnership>Individual Applicant</star:VehicleOwnership>" +
                  "<star:DealerProducts>" +
                     "<star:DealerProductsType>Other</star:DealerProductsType>" +
                     "<star:DealerProductsAmount currency=\"USD\">4699.00</star:DealerProductsAmount>" +
                     "<star:DealerProductsPaidFor>Combined Other</star:DealerProductsPaidFor>" +
                     "<star:DealerProductsCapitalizedInd>1</star:DealerProductsCapitalizedInd>" +
                     "<star:PreferredProviderIndicator>0</star:PreferredProviderIndicator>" +
                  "</star:DealerProducts>" +
               "</star:CreditVehicle>" +
               "<star:Financing>" +
                  "<star:ContractDate>2016-11-30</star:ContractDate>" +
                  "<star:ContractTerm length=\"Months\">48</star:ContractTerm>" +
                  "<star:HardAddSellingPrice currency=\"USD\">0</star:HardAddSellingPrice>" +
                  "<star:TotalDownPaymentAmount currency=\"USD\">1542.8</star:TotalDownPaymentAmount>" +
                  "<star:CashDownPayment currency=\"USD\">5940.49</star:CashDownPayment>" +
                  "<star:AnnualMilesAllowed uom=\"M\">12000</star:AnnualMilesAllowed>" +
                  "<star:ExcessMileageRate currency=\"USD\">1.00</star:ExcessMileageRate>" +
                  "<star:FederalTILDisclosures>" +
                     "<star:FinanceCharge currency=\"USD\">28086.8</star:FinanceCharge>" +
                     "<star:TotalOfPayments currency=\"USD\">115849.97</star:TotalOfPayments>" +
                     "<star:FirstPaymentDate>2016-11-30</star:FirstPaymentDate>" +
                     "<star:PaymentSchedule>" +
                        "<star:NumberOfPayments>1</star:NumberOfPayments>" +
                        "<star:PaymentAmount currency=\"USD\">2325.84</star:PaymentAmount>" +
                        "<star:PayTerms>01</star:PayTerms>" +
                        "<star:TimeBetweenPayments period=\"MO\">1</star:TimeBetweenPayments>" +
                        "<star:ScheduleStartDate>2016-11-30</star:ScheduleStartDate>" +
                        "<star:BasePaymentAmount currency=\"USD\">2173.68</star:BasePaymentAmount>" +
                     "</star:PaymentSchedule>" +
                     "<star:PaymentSchedule>" +
                        "<star:NumberOfPayments>47</star:NumberOfPayments>" +
                        "<star:PaymentAmount currency=\"USD\">2325.84</star:PaymentAmount>" +
                        "<star:PayTerms>01</star:PayTerms>" +
                        "<star:TimeBetweenPayments period=\"MO\">1</star:TimeBetweenPayments>" +
                        "<star:ScheduleStartDate>2017-01-01</star:ScheduleStartDate>" +
                        "<star:BasePaymentAmount currency=\"USD\">2173.68</star:BasePaymentAmount>" +
                     "</star:PaymentSchedule>" +
                     "<star:LeaseRateMoneyFactor>0.00258</star:LeaseRateMoneyFactor>" +
                  "</star:FederalTILDisclosures>" +
                  "<star:ProgramsAndRates>" +
                     "<star:BalloonResidualPercentage>49.00</star:BalloonResidualPercentage>" +
                     "<star:ContractTermMileage uom=\"M\">48000</star:ContractTermMileage>" +
                     "<star:SpecialProgramDetail>" +
                        "<star:SpecialPrograms>H02</star:SpecialPrograms>" +
                     "</star:SpecialProgramDetail>" +
                     "<star:LeaseBuyMoneyFactor>0.00230</star:LeaseBuyMoneyFactor>" +
                     "<star:DiscountRate>0</star:DiscountRate>" +
                  "</star:ProgramsAndRates>" +
                  "<star:Tax>" +
                     "<star:TaxType>Monthly/Use</star:TaxType>" +
                     "<star:TaxDescription>Combined Detail</star:TaxDescription>" +
                     "<star:TaxAmount currency=\"USD\">152.16</star:TaxAmount>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                     "<star:CapitalizedTaxInd>1</star:CapitalizedTaxInd>" +
                  "</star:Tax>" +
                  "<star:Tax>" +
                     "<star:TaxType>Total Monthly/Use</star:TaxType>" +
                     "<star:TaxDescription>Total</star:TaxDescription>" +
                     "<star:TaxAmount currency=\"USD\">152.16</star:TaxAmount>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>>" +
                     "<star:CapitalizedTaxInd>1</star:CapitalizedTaxInd>" +
                  "</star:Tax>" +
                  "<star:Tax>" +
                     "<star:TaxType>CapCostReduction</star:TaxType>" +
                     "<star:TaxDescription>Total</star:TaxDescription>" +
                     "<star:TaxAmount currency=\"USD\">230.5</star:TaxAmount>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                     "<star:CapitalizedTaxInd>0</star:CapitalizedTaxInd>" +
                  "</star:Tax>" +
                  "<star:Fee>" +
                     "<star:FeeType>LicenseFee</star:FeeType>" +
                     "<star:FeeDescription>LicenseFee</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">88.85</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>DocumentationFee</star:FeeType>" +
                     "<star:FeeDescription>SERVICE FEE</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">589</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>1</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>LemonLawFeePaidToGovernmentAgency</star:FeeType>" +
                     "<star:FeeDescription>FloridaMVWEAFee</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">2</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>AcquisitionFee</star:FeeType>" +
                     "<star:FeeDescription>AcquisitionFee</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">795</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>1</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>TireFee</star:FeeType>" +
                     "<star:FeeDescription>TireFeefromTireAndBattery</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">66.5</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>TurnInFee</star:FeeType>" +
                     "<star:FeeDescription>DispositionFee</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">595</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>PurchaseOptionFee</star:FeeType>" +
                     "<star:FeeDescription>PurchaseOptionFee</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">150</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>0</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:Fee>" +
                     "<star:FeeType>AcquisitionFeeMarkup</star:FeeType>" +
                     "<star:FeeDescription>AcquisitionFeeMarkup</star:FeeDescription>" +
                     "<star:FeeAmount currency=\"USD\">300</star:FeeAmount>" +
                     "<star:CapitalizedFeeInd>0</star:CapitalizedFeeInd>" +
                     "<star:TaxabilityInd>1</star:TaxabilityInd>" +
                  "</star:Fee>" +
                  "<star:TotalAmountPaidToInsuranceCo currency=\"USD\">0</star:TotalAmountPaidToInsuranceCo>" +
                  "<star:SecurityDepositAmount currency=\"USD\">0</star:SecurityDepositAmount>" +
                  "<star:DepreciationAndAmortizedAmts currency=\"USD\">76249.84</star:DepreciationAndAmortizedAmts>" +
                  "<star:TotalAmtOfBaseMonthlyPayments currency=\"USD\">104336.64</star:TotalAmtOfBaseMonthlyPayments>" +
                  "<star:TotalEstimatedFeesAndTaxesAmt currency=\"USD\">7691.53</star:TotalEstimatedFeesAndTaxesAmt>" +
                  "<star:ResidualAmount currency=\"USD\">75276.25</star:ResidualAmount>" +
                  "<star:BaseResidualAmount currency=\"USD\">75276.25</star:BaseResidualAmount>" +
                  "<star:FinalPaymentDate>2020-11-01</star:FinalPaymentDate>" +
                  "<star:TaxExempt>0</star:TaxExempt>" +
                  "<star:TotalOfMonthlyPaymentsAmount currency=\"USD\">111640.32</star:TotalOfMonthlyPaymentsAmount>" +
                  "<star:TotalDueAtSigningAmount currency=\"USD\">5940.49</star:TotalDueAtSigningAmount>" +
                  "<star:MonthlyDepreciationAmount currency=\"USD\">1588.54</star:MonthlyDepreciationAmount>" +
                  "<star:NetCapCostPlusResidualAmount currency=\"USD\">226802.34</star:NetCapCostPlusResidualAmount>" +
                  "<star:PurchaseOptionPrice currency=\"USD\">75276.25</star:PurchaseOptionPrice>" +
                  "<star:MinUnusedPurchaseDistance uom=\"M\">48000</star:MinUnusedPurchaseDistance>" +
                  "<star:MaxUnusedPurchaseDistance>48000</star:MaxUnusedPurchaseDistance>" +
                  "<star:ServiceChargeAmount currency=\"USD\">28086.8</star:ServiceChargeAmount>" +
                  "<star:GrossProceedsAmount currency=\"USD\">148405.25</star:GrossProceedsAmount>" +
                  "<star:LeaseMaturityDate>2020-12-01</star:LeaseMaturityDate>" +
                  "<star:CapReductionCashDownpaymentAmt currency=\"USD\">1542.80</star:CapReductionCashDownpaymentAmt>" +
                  "<star:CapReductionMfgRebateAmt currency=\"USD\">0.00</star:CapReductionMfgRebateAmt>" +
                  "<star:CapReductNetTradeDownpymtAmt currency=\"USD\">0</star:CapReductNetTradeDownpymtAmt>" +
                  "<star:AutoBroker>" +
                     "<star:PartyId>0</star:PartyId>" +
                  "</star:AutoBroker>" +
                  "<star:DemoResidualAdjustmentRate>0.20</star:DemoResidualAdjustmentRate>" +
               "</star:Financing>" +
            "</star:CreditContract></star:DataArea>";
    	
        Node secret = encryptXMLSym(message, "AES", "DESede", publicKey);
        String encryptedData = XmlUtil.serializeAsString(secret,true);
        System.out.println(encryptedData);
        String encryptedMessage = FileUtil.readFile("D:\\MBFS_Encryption\\EncryptedText.txt");
        Node decryptNode = decryptXMLSym(encryptedMessage, "AES", XMLCipher.RSA_v1dot5, privateKey);
        String decryptedData = XmlUtil.serializeAsString(decryptNode,true);
        System.out.println(decryptedData);
       /* FileOutputStream fos = new FileOutputStream("D:\\encrypted.txt");
        fos.write(secret);
        fos.close();
        byte[] recovered_message = decrypt(secret, privateKey);
        System.out.println(new String(recovered_message));*/
	}

}
