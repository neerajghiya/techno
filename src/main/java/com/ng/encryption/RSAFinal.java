import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Neeraj Ghiya
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
        
        byte[] bytes = rawText.getBytes("UTF-8");
        
        byte[] encrypted = blockCipher(bytes,cipher, Cipher.PUBLIC_KEY);
        
        return encrypted;
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
    	Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.PRIVATE_KEY, privateKey);
        
        byte[] decrypted = blockCipher(cipherText,cipher, Cipher.PRIVATE_KEY);

        return decrypted;
    }
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
    	PublicKey publicKey = getPublicKey("D:\\MBFS_Encryption\\id_rsa.pub");
    	PrivateKey privateKey = getPrivateKey("D:\\MBFS_Encryption\\id_rsa");
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
    	
    	String message = "<star:Process acknowledge=\"Always\" confirm=\"Always\"/>" +
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
            "</star:CreditContract>";
    	
        byte[] secret = encrypt(message, publicKey);
        //System.out.println(new String(secret, "UTF8"));
        byte[] recovered_message = decrypt(secret, privateKey);
        System.out.println(new String(recovered_message));
	}
    
    private static byte[] blockCipher(byte[] bytes, Cipher cipher, int mode) throws IllegalBlockSizeException, BadPaddingException{
    	// scrambled will hold intermediate results
    	byte[] scrambled = new byte[0];

    	// toReturn will hold the total result
    	byte[] toReturn = new byte[0];
    	// if we encrypt we use 245 byte long blocks. Decryption requires 256 byte long blocks (because of RSA)
    	int length = (mode == Cipher.PUBLIC_KEY)? 245 : 256;

    	// another buffer. this one will hold the bytes that have to be modified in this step
    	byte[] buffer = new byte[length];

    	for (int i=0; i< bytes.length; i++){

    		// if we filled our buffer array we have our block ready for de- or encryption
    		if ((i > 0) && (i % length == 0)){
    			scrambled = cipher.doFinal(buffer);
    			toReturn = append(toReturn,scrambled);
    			// here we calculate the length of the next buffer required
    			int newlength = length;

    			// if newlength would be longer than remaining bytes in the bytes array we shorten it.
    			if (i + length > bytes.length) {
    				 newlength = bytes.length - i;
    			}
    			// clean the buffer array
    			buffer = new byte[newlength];
    		}
    		// copy byte into our buffer.
    		buffer[i%length] = bytes[i];
    	}

    	// this step is needed if we had a trailing buffer. should only happen when encrypting.
    	// example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
    	scrambled = cipher.doFinal(buffer);

    	// final step before we can return the modified data.
    	toReturn = append(toReturn,scrambled);

    	return toReturn;
    }
    
    private static byte[] append(byte[] prefix, byte[] suffix){
    	byte[] toReturn = new byte[prefix.length + suffix.length];
    	for (int i=0; i< prefix.length; i++){
    		toReturn[i] = prefix[i];
    	}
    	for (int i=0; i< suffix.length; i++){
    		toReturn[i+prefix.length] = suffix[i];
    	}
    	return toReturn;
    }
}
