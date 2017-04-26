import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

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

public class StarLeadDupExcepMonitorCronService {

	public static void main(String[] args) throws IOException {
		StarLeadDupExcepMonitorCronService cronService = new StarLeadDupExcepMonitorCronService();
		cronService.readAndRunDuplicateFailureFiles();

	}
	
	private void readAndRunDuplicateFailureFiles() throws IOException {
		BufferedReader br = null;
		StringBuffer requstTimes = new StringBuffer();

		String failureFilePath = "/usr/appdata/share/eai/starLead/duplicatePushLead2EAI/failure";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String todayFilePath = failureFilePath + "/" + dateFormat.format(cal.getTime());
		System.out.println("todaysFilePath = " + todayFilePath);
		
		File file = new File(todayFilePath);
		if (file.exists()) {
			int succuessCount = 0;
			
			System.out.println("File exists");
			
			File[] paths = file.listFiles();
			for (File path : paths) {
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
					
					String requestTime = getElementValue(xml, "/Message/MessageHeader/RequestTime");
					int startIndex = requestTime.length() - 6;
					String timeZone = requestTime.substring(startIndex, requestTime.length());
					System.out.println("requestTime = " + requestTime + " timezone = " + timeZone);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					
					try {
						final Random random = new Random();
						Date date = sdf.parse(requestTime);
						Long time = date.getTime();
						time = time + random.nextInt(1000);
						String formattedTime = sdf.format(time);
						formattedTime = formattedTime + timeZone;
						System.out.println("formattedTime = " + formattedTime);
						xml = changeRequestTime(xml, formattedTime);
					} catch (ParseException e) {
						System.out.println("ParseException" + e  );
						requstTimes.append(requestTime);
					}
					
					sendRestCall(xml);
					path.delete();
					
					
				} catch (FileNotFoundException e) {
					System.out.println("FileNotFoundException = " + e);
				} catch (IOException e) {
					System.out.println("IOException = " + e);
				} finally {
					br.close();
				}
			}
		
			sendMail("Total Failed Files : " + paths.length + "\n  Failed to parse request time = " + "( " + requstTimes + ")");
		}

	}
	
	private void sendRestCall(String xml) {
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
	
	private String getElementValue(String xml, String xPath){
		Document doc = parseAsXml(xml);
		Element root = doc.getDocumentElement();
		String nodeValue = getNodeValue(root, xPath);
		
		return nodeValue;
	}
	
	private static String getNodeValue(Element element, String xQuery) {
		String nodeValue = "";
		try {
			Node elementNode = XPathAPI.selectSingleNode(element, xQuery);
			if (elementNode != null) {
				Node node = elementNode.getLastChild();
				if (node != null) {
					nodeValue = node.getNodeValue();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nodeValue;
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
			
			msg.setSubject("-[Star Leads-PROD] Application Automated Result(DuplicateKey)-");
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
	
	private String changeRequestTime(String xml, String requestTime) {
		Document doc = parseAsXml(xml);
		Element root = doc.getDocumentElement();
		setXNodeValue(root, "/Message/MessageHeader/RequestTime", requestTime);
		String finalInput = serializeAsString(doc.getDocumentElement(),true);
		return finalInput;
	}
	
	private void setXNodeValue(Element element, String xQuery,	String nodeValue) {
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
	
	private String serializeAsString(Node node, boolean pretty) {
		return serializeAsString(node, null, pretty);
	}

	private String serializeAsString(Node node, String[] escapableTags,
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

	private void serializeXML(Node node, Writer writer,
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

}
