import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class OnlineLeadGatewayUpdateService {
	public static void main(String[] args) throws IOException {
		OnlineLeadGatewayUpdateService cronService = new OnlineLeadGatewayUpdateService();
		cronService.readAndRunFailureFiles();

	}
	
	private void readAndRunFailureFiles() throws IOException {
		BufferedReader br = null;
		StringBuffer requstTimes = new StringBuffer();
		StringBuffer successLeads = new StringBuffer();

		String failureFilePath = "/StarEAI/DPLogs/OnlineLeads/failed/UpdateOnlineLeadServlet";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String todayFilePath = failureFilePath + "/" + dateFormat.format(cal.getTime());
		System.out.println("todaysFilePath = " + todayFilePath);
		
		File file = new File(todayFilePath);
		if (file.exists()) {
			
			List leadList = readCSVFile();
			
			System.out.println("File exists");
			int totalFileExecuted = 0;
			File[] paths = file.listFiles();
			for (File path : paths) {
				if (!leadList.contains(path.getName())) {
					
					// prints file and directory paths
					System.out.println("File Path = " + path);

					try {
						br = new BufferedReader(new FileReader(path));
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();
						System.out.println("line = " + line);
						while (line != null) {
							sb.append(line);
							sb.append("\n");
							line = br.readLine();
						}
						String xml = sb.toString();
						

						sendRestCall(xml);
						totalFileExecuted++;
						successLeads.append(path.getName());
						successLeads.append(",");
						if(leadList.size() > 0 || totalFileExecuted >0){
							updateFile(path.getName(), true);
						}else{
							updateFile(path.getName(), false);
						}

					} catch (FileNotFoundException e) {
						System.out.println("FileNotFoundException = " + e);
					} catch (IOException e) {
						System.out.println("IOException = " + e);
					} finally {
						br.close();
					}
				}
			}
			
			if(totalFileExecuted > 0){
				sendMail("Total Failed Files : " + totalFileExecuted
						+ "\n  Failed to parse request time = " + "( "
						+ requstTimes + ")" + "Successfully executed = " + "( "
						+ successLeads + ")");
			}
		}

	}
	
	private void sendRestCall(String xml) {
		System.out.println("Inside sendRestCall with xml = " + xml);
		try {
			URL url = new URL("http://starleadeai-qa.usfdc.corpintra.net/StarLeadEAIWeb/UpdateOnlineLeadServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/xml");

			String input = xml;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
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
	
	public static Document parseAsXml(String xmlString) {
		Document doc = null;
		if (xmlString != null) {
			try {
				xmlString = getUTF8Encoded(xmlString);

				StringReader stringReader = new StringReader(xmlString);
				InputSource inputSource = new InputSource();
				inputSource.setCharacterStream(stringReader);
				doc = parse(inputSource);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return doc;
	}
	
	public static String getUTF8Encoded(String xmlString)
			throws UnsupportedEncodingException {
		xmlString = new String(xmlString.getBytes("UTF-8"));
		return xmlString;
	}
	
	public static Document parse(Object theStream) {
		Document document = null;
		try {
			InputSource inputSource = null;
			InputStream inputStream = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			factory.setValidating(false);
			factory.setNamespaceAware(false);
			if (theStream instanceof InputSource) {
				inputSource = (InputSource) theStream;
				document = factory.newDocumentBuilder().parse(inputSource);
			} else if (theStream instanceof InputStream) {
				inputStream = (InputStream) theStream;

				document = factory.newDocumentBuilder().parse(inputStream);
			}
			return document;
		} catch (Exception e) {
			throw new RuntimeException("XMLUtil: Failed to parse XML"
					+ e.getMessage());
		}
	}
	
	private void sendMail(String body)	  {
		try
		{
			Properties props = System.getProperties();
			props.put("mail.host", "mailhost.americas.bg.corpintra.net");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.user", "starlead-eai-hub@mbusa.com");
			props.put("mail.from", "starlead-eai-hub@mbusa.com");
			
			

			Session mailSession = Session.getDefaultInstance(props, null);

			MimeMessage msg = new MimeMessage(mailSession);

			String from = "starlead-eai-hub@mbusa.com";
			InternetAddress sender = new InternetAddress(from, from );
			msg.setFrom(sender);

			String to = "sga.jain@mbusa.com, sga.ghiya@mbusa.com";
			Address[] address = (Address[]) InternetAddress.parse(to, false);
			msg.addRecipients(Message.RecipientType.TO, address);
			System.out.println( "address: set");
			
			msg.setSubject("-[Star Leads-PROD] Application Automated Result(DuplicateKey - OnlineLeads)-");
			msg.setText(body);
			
			msg.setHeader("MIME-Version", "1.0");
			msg.setHeader("Content-Type", "text/html; charset=\"us-ascii\"");

			msg.setSentDate(new Date());

			Transport.send(msg);
			System.out.println( "Message Sent to:" + to );

		}
		catch (AddressException ae)
		{ System.out.println(ae.getMessage());
			}
		catch (MessagingException me)
		{ System.out.println(me.getMessage());

		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	
	
	private static List readCSVFile() throws IOException {
		List<String> leadList = new ArrayList<String>();
		String csvFilePath = "/home/stareai/StarLead";
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
		String[] leads = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			File file = new File(csvFilePath + "/" + dateFormat.format(cal.getTime()) + "/onlineGatewayUpdateLeads.csv");
			if (file.exists()) {
				System.out.println("File exists");
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {

					// use comma as separator
					leads = line.split(cvsSplitBy);

				}
			}else{
				file.getParentFile().mkdir();
				file.createNewFile();
			}
			if(leads != null && leads.length > 0){
				for (String lead : leads) {
					System.out.println(lead);
					leadList.add(lead);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException while reading csv= " + e);
		} catch (IOException e) {
			System.out.println("IOException while reading csv= " + e);
		} finally {
			if(br != null){
				br.close();
			}
		}
		return leadList;
	}
	
	private static void updateFile(String lead, boolean appendComma) throws IOException{
		String csvFilePath = "/home/stareai/StarLead";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			FileWriter file = new FileWriter(csvFilePath + "/"
					+ dateFormat.format(cal.getTime()) + "/onlineGatewayUpdateLeads.csv", true);
			if (appendComma) {
				file.append(",");
			}
			file.append(lead);
			file.flush();
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException while updating csv= " + e);
		} catch (IOException e) {
			System.out.println("IOException while reading updating= " + e);
		}
	}
	
}
