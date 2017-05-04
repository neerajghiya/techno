import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


public class StarLeadShiftDigitalCronService {
	
	public static void main(String[] args) throws IOException {
		StringBuffer documentIds = new StringBuffer();
		String filePath = "/usr/appdata/share/eai/dplogs/starleads/failed/p2vQueue";
		
		File file = new File(filePath);
		
		File[] files = file.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	boolean accept = false;
		    	List leadList = null;
		    	try {
					 leadList = readCSVFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	if(!leadList.contains(name) && name.endsWith(".xml")){
		    		accept = true;
		    	}
		    	return accept;
		    }
		}); 
		System.out.println("file: " + file);
		BufferedReader br = null;
		for (int i=0;i< files.length;i++){
			System.out.println(files[i].getName());
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath+"/"+files[i].getName())));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			System.out.println("line = " + line);
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			String xml = sb.toString();
			xml = changeBusinessServiceName(xml, "P2VQueue");
			documentIds.append(files[i].getName());
			sendRestCall(xml);
			updateFile(files[i].getName(), true);
		}
		sendMail("Total Failed Files : " + files.length + "\n  File Name = " + "( " + documentIds + ")");
	}
	
	private static void sendMail(String body)	  {
		try
		{
			Properties props = System.getProperties();
			props.put("mail.host", "mailhost.americas.bg.corpintra.net");
			//props.put("mail.host", "smtp.gmail.com");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.user", "starlead-eai-hub@mbusa.com");
			props.put("mail.from", "starlead-eai-hub@mbusa.com");
			//props.put("mail.smtp.port", "587");
			//props.put("mail.smtp.auth", "false");
			//props.put("mail.smtp.starttls.enable", "false");
			

			Session mailSession = Session.getDefaultInstance(props, null);

			MimeMessage msg = new MimeMessage(mailSession);

			String from = "starlead-eai-hub@mbusa.com";
			InternetAddress sender = new InternetAddress(from, from );
			msg.setFrom(sender);

			String to = "sga.jain@mbusa.com, sga.ghiya@mbusa.com";
			Address[] address = (Address[]) InternetAddress.parse(to, false);
			msg.addRecipients(Message.RecipientType.TO, address);
			System.out.println( "address: set");
			
			msg.setSubject("-[Star Leads-PROD] Application Automated Result(ShiftDigital)-");
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
	
	
	
	private static void sendRestCall(String xml) {
		System.out.println("Inside sendRestCall with xml = " + xml);
		try {
			URL url = new URL("http://starleadeai.usfdc.corpintra.net/StarLeadEAIWeb/MessageServlet");
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
	
	private static String changeBusinessServiceName(String xml, String serviceName) {
		Document doc = parseAsXml(xml);
		Element root = doc.getDocumentElement();
		setXNodeValue(root, "/Message/MessageHeader/BusinessService", serviceName);
		String finalInput = serializeAsString(doc.getDocumentElement(),true);
		return finalInput;
	}
	
	private static void setXNodeValue(Element element, String xQuery,	String nodeValue) {
		try {
			Node node = XPathAPI.selectSingleNode(element, xQuery);
			if (node != null) {
				Node lastChild = node.getLastChild();
				if (lastChild != null)
					lastChild.setNodeValue(nodeValue);
				else
					node.appendChild(element.getOwnerDocument().createTextNode(
							nodeValue));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static String serializeAsString(Node node, boolean pretty) {
		return serializeAsString(node, null, pretty);
	}

	private static String serializeAsString(Node node, String[] escapableTags,
			boolean pretty) {
		StringBuffer sB = new StringBuffer();
		StringWriter sw = new StringWriter();
		try {
			serializeXML(node, sw, escapableTags, pretty);
			sB.append(sw.toString());
		} catch (Exception ex) {
			System.out.println("Exception Caught:: " + ex);
			ex.printStackTrace();
		} finally {
			try {
				sw.close();
			} catch (Exception localException2) {
			}
		}
		return sB.toString();
	}

	private static void serializeXML(Node node, Writer writer,
			String[] escapableTags, boolean pretty) {
		try {
			if ((node == null) || (writer == null)) {
				System.err
						.println("serializeAsString call has a null parameter for: "
								+ ((node == null) ? "node" : "writer"));
			}
			OutputFormat outputFormat = new OutputFormat();
			outputFormat.setOmitXMLDeclaration(true);
			if ((escapableTags != null) && (escapableTags.length > 0)) {
				outputFormat.setNonEscapingElements(escapableTags);
			}
			if (pretty) {
				outputFormat.setIndenting(true);
				outputFormat.setLineSeparator("\n");
			}
			XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
			serializer.asDOMSerializer();
			serializer.serialize((Element) node);
		} catch (IOException io) {
			io.printStackTrace();
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
			File file = new File(csvFilePath + "/" + dateFormat.format(cal.getTime()) + "/shiftLeads.csv");
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
					+ dateFormat.format(cal.getTime()) + "/shiftLeads.csv", true);
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
