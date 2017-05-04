import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mbusa.mef.util.FileUtil;
import com.mbusa.mef.util.StringUtil;
import com.mbusa.mef.util.XmlUtil;


public class CPSAutomatedTrigger {

	public static void main(String[] args) throws IOException {
		//String filePath = "D:\\CPS\\CPS_Posting\\";  //Local Test
		String filePath = "/usr/appdata/share/eai/sapeaifin-cofico/CPS_Posting/";
		File file = new File(filePath);
		
		File[] files = file.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return name.endsWith(".log");
		    }
		}); 
		System.out.println("file: " + file);
		
		Calendar cal = Calendar.getInstance();
	    System.out.println(cal.getTime());
	    System.out.println(cal.getTime());
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String dt = sdf.format(cal.getTime());
	    System.out.println("dt: " + dt);
	    
	    for (int i=0;i< files.length;i++){
	    	if (files[i].getName().indexOf(dt )> -1 ) {
	    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+"/"+files[i].getName())));
				String readLn=null;
				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				sb.append("<Root>");
				while((readLn=br.readLine())!=null){
					sb.append(readLn);
				}
				sb.append("</Root>");
				System.out.println("sb" + sb.toString());
				Document doc = XmlUtil.parseAsXml(sb.toString());
				Element element = doc.getDocumentElement();
				Iterator it = XmlUtil.getXNodeIterator(element, "//Response" );
				List<String> invoiceList = new ArrayList<String>();
				while( it.hasNext() ){
			    	Element recordElement = (Element)it.next();
			    	String invNumber = XmlUtil.getXNodeValue(recordElement, "InvoiceNumber" );
			    	String sapResp = XmlUtil.getXNodeValue(recordElement, "SAPResponse" );
			    	String dbError = XmlUtil.getXNodeValue(recordElement, "Error" );
			    	if(!StringUtil.isNullOrEmpty(dbError) && !("Invoice exists".equals(sapResp))){
			    		System.out.println(invNumber + " " + sapResp + " " + dbError);
			    		
			    		callRestAPI(invNumber, sapResp, invoiceList);
			    	}
				}
				String responseMessage = "Invoice successfully updated = {" + invoiceList.toString() + "}";
				System.out.println(responseMessage);
				//String xml = FileUtil.readFile("D:\\MbusaGit\\Code\\Other\\mailContent.xml");
				String xml = FileUtil.readFile("/home/neghiya/CPSTrigger/mailContent.xml");
				System.out.println(xml);
				xml = changeXmlContent(xml, responseMessage);
				System.out.println(xml);
				sendMail(xml);
	    	}
	    }
	    

	}

	private static void callRestAPI(String invNumber, String sapResp, List<String> invoiceList) {
		System.out.println("Inside sendRestCall ");
		try {
			//Test URL
			//URL url = new URL("http://mbhobgnapp801.americas.bg.corpintra.net:9080/sapEAIServicesWeb/sapeaiservices?action=updateInvoiceDetails&numInvoice="+invNumber+"&sapDocumentNumber="+sapResp);//new URL(StarleadUtil.getRestLeadUpdateUrl("leadUpdate"));
			
			//QA URL
			URL url = new URL("http://mbfinapps.usfdc.corpintra.net/sapEAIServicesWeb/sapeaiservices?action=updateInvoiceDetails&numInvoice="+invNumber+"&sapDocumentNumber="+sapResp);
			System.out.println("Inside sendRestCall " + url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "text/xml");
			conn.setRequestProperty("appid", "eai");

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("Success : HTTP error code : " + conn.getResponseCode());
				invoiceList.add(invNumber);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			StringBuilder sb = new StringBuilder();

			String output;
			System.out.println("Output from Server .... \n" + conn.getResponseMessage());
			while ((output = br.readLine()) != null) {
				sb.append(output);
				sb.append("\n");
				System.out.println(output);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException = " + e);
		} catch (IOException e) {
			System.out.println("IOException = " + e);
		}
		
	}
	
	private static void sendMail(String xmlMessage) {
		System.out.println("Inside sendRestCall ");
		try {
			byte[] postData = xmlMessage.getBytes("UTF-8");
			int    postDataLength = postData.length;
			URL url = new URL("http://eaimail.usfdc.corpintra.net/MailHubWeb/MailHubRestService");//new URL(StarleadUtil.getRestLeadUpdateUrl("leadUpdate"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );

			OutputStream os = conn.getOutputStream();
			/*try( DataOutputStream wr = new DataOutputStream( os)) {
				   wr.write( postData );
				}*/
			os.write(postData);
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
				//throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			StringBuilder sb = new StringBuilder();

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				sb.append(output);
				sb.append("\n");
				System.out.println(output);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException = " + e);
		} catch (IOException e) {
			System.out.println("IOException = " + e);
		}
		
	}
	
	private static String changeXmlContent(String xml, String xmlContent) {
		Document doc = XmlUtil.parseAsXml(xml);
		Element root = doc.getDocumentElement();
		XmlUtil.setXNodeValue(root, "/Workflow/Input/Email/EmailContent", xmlContent);
		String finalInput = XmlUtil.serializeAsString(doc.getDocumentElement(),true);
		return finalInput;
	}

}
