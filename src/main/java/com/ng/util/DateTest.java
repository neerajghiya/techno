import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTest {

	public static void main(String[] args) {
		//testOne();
		testTwo();

	}

	private static void testOne() {
		String failureFilePath = "/usr/appdata/share/eai/starLead/leadBatchUpdate/failure";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String todaysFilePath = failureFilePath + "/" + dateFormat.format(cal.getTime());
		System.out.println("todaysFilePath = " + todaysFilePath);
		
	}
	
	private static void testTwo() {
		String failureFilePath = "/usr/appdata/share/eai/starLead/leadBatchUpdate/failure";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String todaysFilePath = failureFilePath + "/" + dateFormat.format(cal.getTime());
		System.out.println("todaysFilePath = " + todaysFilePath);
		
	}

}
