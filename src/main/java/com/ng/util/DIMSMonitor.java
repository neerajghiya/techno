import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DIMSMonitor {
	
	private static final String dailyFileInboundPath = "/DealerPartsInventory/dims/cdk/ArchiveFiles/DailyFile";
	private static final String expectedOrderOutboundFilePath = "/DealerPartsInventory/dims/cdk/LogFiles/ExpectedOrderFile";
	private static final String materialPlanFileInboundPath = "/DealerPartsInventory/dims/cdk/ArchiveFiles/PartsSpecificParam";
	private static final String materialPlanFileOutboundPath = "/DealerPartsInventory/dims/cdk/LogFiles/PartsSpecificParam";
	private static final String assortmentExitFileOutboundPath = "/DealerPartsInventory/dims/cdk/LogFiles/AssortmentExit";

	public static void main(String[] args) throws IOException {
		DIMSMonitor cronService = new DIMSMonitor();
		Integer dailyFileInboundCount = cronService.dailyFilesMonitor();
		Integer expectedOrderOutboundCount = cronService.expectedOrderFilesMonitor();
		Integer materialPlanInboundCount = cronService.materialPlanFilesInboundMonitor();
		Integer materialPlanOutboundCount = cronService.materialPlanFilesOutboundMonitor();
		Integer assortmentExitOutboundCount = cronService.assortmentExitFilesOutboundMonitor();
				
		StringBuffer body = new StringBuffer();
		body.append("<b><u>Inbound</u></b>");
		body.append("\n<br>Dims Daily File Count :");
		body.append(dailyFileInboundCount);
		body.append("\n<br>Material Plan Count :");
		body.append(materialPlanInboundCount);
		body.append("\n<br>\n<br><b><u>Outbound</u></b>");
		body.append("\n<br>Expected Order Count :");
		body.append(expectedOrderOutboundCount);
		body.append("\n<br>Material Plan Count :");
		body.append(materialPlanOutboundCount);
		body.append("\n<br>Assortment Exit Count :");
		body.append(assortmentExitOutboundCount);
		
		System.out.println(body);
		cronService.sendMail(body.toString());
	}
	
	private Integer dailyFilesMonitor() throws IOException {
		Integer count = 0;
		
		String filePath = dailyFileInboundPath;
		System.out.println("FilePath = " + filePath);
		
		count = getFileCount(filePath);
		
		return count;
	}

	private int expectedOrderFilesMonitor() throws IOException {
		Integer count = 0;
		
		String filePath = expectedOrderOutboundFilePath;
		System.out.println("FilePath = " + filePath);
		
		count = getFileCount(filePath);
		
		return count;
	}
	
	private int materialPlanFilesInboundMonitor() throws IOException {
		Integer count = 0;
		
		String filePath = materialPlanFileInboundPath;
		System.out.println("FilePath = " + filePath);
		
		count = getAllFileCountForWholeDay(filePath);
		
		return count;
	}
	
	private int materialPlanFilesOutboundMonitor() throws IOException {
		Integer count = 0;
		
		String filePath = materialPlanFileOutboundPath;
		System.out.println("FilePath = " + filePath);
		
		count = getAllFileCountForWholeDay(filePath);
		
		return count;
	}
	
	private int assortmentExitFilesOutboundMonitor() throws IOException {
		Integer count = 0;
		
		String filePath = assortmentExitFileOutboundPath;
		System.out.println("FilePath = " + filePath);
		
		count = getAllFileCountForWholeDay(filePath);
		
		return count;
	}
	
	private Integer getAllFileCountForWholeDay(String filePath)	throws IOException {
		Integer count = 0;
		File file = new File(filePath);
		
		File[] files = file.listFiles(new FilenameFilter() {
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String todaysDate = sdf.format(cal.getTime());
			
		@Override
		public boolean accept(File dir, String name) {
			
			//cal.add(Calendar.DATE, -1);
		   	
		   	Date lastModifiedTime = new Date(new File(dir, name).lastModified());
		   	
		   	
		    String current = sdf.format(lastModifiedTime);
		    
		    return ((todaysDate.equals(current)));
		}
		});
		
		count = files.length;
		
		return count;
	}
	
	private Integer getFileCount(String filePath)
			throws IOException {
		Integer count = 0;
		File file = new File(filePath);
		
		File[] files = file.listFiles(new FilenameFilter() {
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStart;
		String dateEnd;
			
		@Override
		public boolean accept(File dir, String name) {
			Calendar cal = Calendar.getInstance();
		//	cal.add(Calendar.DATE, -6);
			cal.set(Calendar.HOUR_OF_DAY, 3);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		   	dateEnd = sdf.format(cal.getTime());
		   	
		   	cal.add(Calendar.DATE, -1);
		   	cal.set(Calendar.HOUR_OF_DAY, 20);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		   	dateStart = sdf.format(cal.getTime());
		   	
		   	//System.out.println("dateStart = " + dateStart);
		   	//System.out.println("dateEnd = " + dateEnd);
		   	
		   	Date lastModifiedTime = new Date(new File(dir, name).lastModified());
		   	
		   	
		    String current = sdf.format(lastModifiedTime);
		    return ((dateStart.compareTo(current) < 0 
		            && (dateEnd.compareTo(current) >= 0)));
		}
		});
		
		count = files.length;
		
		return count;
	}
	
	private void sendMail(String body)	  {
		try
		{
			Properties props = System.getProperties();
			props.put("mail.host", "mailhost.americas.svc.corpintra.net");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.user", "starlead-eai-hub@mbusa.com");
			props.put("mail.from", "starlead-eai-hub@mbusa.com");
	
			Session mailSession = Session.getDefaultInstance(props, null);
	
			MimeMessage msg = new MimeMessage(mailSession);
	
			String from = "dims-eai-hub@mbusa.com";
			InternetAddress sender = new InternetAddress(from, from );
			msg.setFrom(sender);
	
			String to = "sga.jain@mbusa.com, sga.ghiya@mbusa.com";
			//String to = "sga.ghiya@mbusa.com";
			Address[] address = (Address[]) InternetAddress.parse(to, false);
			msg.addRecipients(Message.RecipientType.TO, address);
			System.out.println( "address: set");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String dateTime = dateFormat.format(cal.getTime());
			
			msg.setSubject("-[DIMS] DAILY MONITORING on " + dateTime);
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

}
