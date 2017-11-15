import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class DIMSMonitorDup {
	
	/*private static final String dailyFilePath = "D:/CronClass/DailyFiles/DealerPartsInventory/dims/logs";
	private static final String expectedOrderSuccessFilePath = "D:/CronClass/DailyFiles/DealerPartsInventory/dims/logs/outbound/success";
	private static final String expectedOrderFailureFilePath = "D:/CronClass/DailyFiles/DealerPartsInventory/dims/logs/outbound/failed";*/
	//private static final String dailyFilePath = "/DealerPartsInventory/dims/logs";
	private static final String dailyFilePath = "/DealerPartsInventory/dims/cdk/ArchiveFiles/DailyFile";
	private static final String expectedOrderFilePath = "/DealerPartsInventory/dims/cdk/LogFiles/ExpectedOrderFile";
	//private static final String expectedOrderSuccessFilePath = "/DealerPartsInventory/dims/logs/outbound/success";
//	private static final String expectedOrderFailureFilePath = "/DealerPartsInventory/dims/logs/outbound/failed";
	private static final String dailyFileNamePrefix = "DailyFileReport_";
	//private static final String expectedOrderFileNamePrefix = "ExpectedOrder_";

	public static void main(String[] args) throws IOException {
		DIMSMonitorDup cronService = new DIMSMonitorDup();
		Integer dailyFileCount = cronService.dailyFilesMonitor();
		Integer expectedOrderCount = cronService.expectedOrderSucessFilesMonitor();
		//Integer expectedOrderFailedCount = cronService.expectedOrderFailedFilesMonitor();
	//	Integer totalExpecteOrderCount = expectedOrderSuccessCount + expectedOrderFailedCount;
		
		StringBuffer body = new StringBuffer();
		body.append("Dims Daily File Count :");
		body.append(dailyFileCount);
		body.append("\n<br>Expected Order Count :");
		body.append(expectedOrderCount);
		
		System.out.println(body);
		cronService.sendMail(body.toString());
	}
	
	private Integer dailyFilesMonitor() throws IOException {
		Integer count = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, -1);
		String dateTime = dateFormat.format(cal.getTime());
		String dailyFileNameSuffix = dateTime.replaceAll("[-.:T]","");
		String todaysFilePath = dailyFilePath + "/" + dailyFileNamePrefix + dailyFileNameSuffix;
		System.out.println("todaysFilePath = " + todaysFilePath);
		
		count = getDimsCount(todaysFilePath, true);
		
		cal.add(Calendar.DATE, -1);
		dateTime = dateFormat.format(cal.getTime());
		dailyFileNameSuffix = dateTime.replaceAll("[-.:T]","");
		String yesterdayFilePath = dailyFilePath + "/" + dailyFileNamePrefix + dailyFileNameSuffix;
		System.out.println("yesterdayFilePath = " + yesterdayFilePath);
		count = count + getDimsCount(yesterdayFilePath, false);
		
		return count;
	}

	private Integer getDimsCount(String todaysFilePath, Boolean todaysFile)
			throws IOException {
		Integer count = 0;
		File file = new File(todaysFilePath);
		if (file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(todaysFilePath));
				String line = br.readLine();
				while (line != null) {
					//System.out.println("line = " + line);
					int beginIndex = line.lastIndexOf(".");
					//System.out.println("beginIndex = " + beginIndex);
					String writeTime = line.substring(beginIndex + 9, beginIndex + 13);
					int lastModifiedTime = Integer.parseInt(writeTime);
					//System.out.println("writeTime = " + lastModifiedTime);
					if(todaysFile){
						if(lastModifiedTime <= 200){
							System.out.println("line = " + line);
							count ++;
						}
					}else{
						if(lastModifiedTime >= 2000){
							System.out.println("line = " + line);
							count ++;
						}
					}
					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException = " + e);
			} catch (IOException e) {
				System.out.println("IOException = " + e);
			} finally {
				br.close();
			}
		}
		return count;
	}
	
	private int expectedOrderSucessFilesMonitor() throws IOException {
		Integer count = 0;
		
	//	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	//	Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, -1);
		//String dateTime = dateFormat.format(cal.getTime());
		String todaysFilePath = expectedOrderFilePath;/* + "/" + expectedOrderFileNamePrefix + dateTime + ".log";*/
		System.out.println("todaysFilePath = " + todaysFilePath);
		
		count = getExpectedOrderCount(todaysFilePath, true);
		
		/*cal.add(Calendar.DATE, -1);
		dateTime = dateFormat.format(cal.getTime());
		String yesterdayFilePath = expectedOrderFilePath + "/" + expectedOrderFileNamePrefix + dateTime + ".log";
		System.out.println("yesterdayFilePath = " + yesterdayFilePath);
		count = count + getExpectedOrderCount(yesterdayFilePath, false);*/
		
		
		return count;
	}
	
	/*private int expectedOrderFailedFilesMonitor() throws IOException {
		Integer count = 0;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, -1);
		String dateTime = dateFormat.format(cal.getTime());
		String todaysFilePath = expectedOrderFailureFilePath + "/" + expectedOrderFileNamePrefix + dateTime + ".log";
		System.out.println("todaysFilePath = " + todaysFilePath);
		
		count = getExpectedOrderCount(todaysFilePath, true);
		
		cal.add(Calendar.DATE, -1);
		dateTime = dateFormat.format(cal.getTime());
		String yesterdayFilePath = expectedOrderFailureFilePath + "/" + expectedOrderFileNamePrefix + dateTime + ".log";
		System.out.println("yesterdayFilePath = " + yesterdayFilePath);
		count = count + getExpectedOrderCount(yesterdayFilePath, false);
		
		
		return count;
	}*/
	
	private Integer getExpectedOrderCount(String todaysFilePath, Boolean todaysFile)
			throws IOException {
		Integer count = 0;
		File file = new File(todaysFilePath);
		
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
		
		/*if (file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(todaysFilePath));
				String line = br.readLine();
				while (line != null) {
					if(line.isEmpty()){
						line = br.readLine();
						continue;
					}
					System.out.println("line = " + line);
					int beginIndex = line.lastIndexOf("_");
					//System.out.println("beginIndex = " + beginIndex);
					String writeTime = line.substring(beginIndex + 12, beginIndex + 17);
					writeTime = writeTime.replaceAll("[-.:T]","");
					//int lastModifiedTime = Integer.parseInt(writeTime);
					int lastModifiedTime = Integer.parseInt(writeTime);
					System.out.println("writeTime = " + lastModifiedTime);
					if(todaysFile){
						if(lastModifiedTime <= 200){
							System.out.println("line = " + line);
							count ++;
						}
					}else{
						if(lastModifiedTime >= 2000){
							System.out.println("line = " + line);
							count ++;
						}
					}
					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException = " + e);
			} catch (IOException e) {
				System.out.println("IOException = " + e);
			} finally {
				br.close();
			}
		}*/
		return count;
	}
	
	private void sendMail(String body)	  {
		try
		{
			Properties props = System.getProperties();
			props.put("mail.host", "mailhost.americas.svc.corpintra.net");
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
	
			String from = "dims-eai-hub@mbusa.com";
			InternetAddress sender = new InternetAddress(from, from );
			msg.setFrom(sender);
	
			String to = "sga.jain@mbusa.com, sga.ghiya@mbusa.com";
			Address[] address = (Address[]) InternetAddress.parse(to, false);
			msg.addRecipients(Message.RecipientType.TO, address);
			System.out.println( "address: set");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.DATE, -1);
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
