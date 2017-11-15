import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTest {

	public static void main(String[] args) {
		//testOne();
		testThree();

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
	
	private static void testThree() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStart;
	    String dateEnd;
		Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, -6);
    	cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
    	dateEnd = dateFormat.format(cal.getTime());
    	
    	cal.add(Calendar.DATE, -1);
    //	cal.add(Calendar.DATE, -1);
    	cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
    	dateStart = dateFormat.format(cal.getTime());
    	
    	System.out.println("dateStart = " + dateStart);
    	System.out.println("dateEnd = " + dateEnd);
		
	}

}
