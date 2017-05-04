import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CSVFile {

	public static void main(String[] args) throws IOException {
		readFile();
		updateFile();
		createFile();

	}

	private static void readFile() throws FileNotFoundException, IOException {
		String failureFilePath = "D:/fileTest";
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
		String[] country = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		File file = new File(failureFilePath + "/" + dateFormat.format(cal.getTime()) + "/dd.txt");
		if (file.exists()) {
			System.out.println("File exists");
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
                // use comma as separator
                country = line.split(cvsSplitBy);
            }
			for (String string : country) {
				System.out.println(string);
			}
		}else{
			file.getParentFile().mkdir();
			file.createNewFile();
		}
		
		
	}
	
	private static void updateFile() throws IOException{
		String failureFilePath = "D:/fileTest";
		FileWriter file = new FileWriter(failureFilePath + "/dd.txt", true);
		file.append(",");
		file.append("115");
		file.flush();
		file.close();
	}
	
	private static void createFile() throws IOException{
		String failureFilePath = "D:/fileTest";
		FileWriter file = new FileWriter(failureFilePath + "/dd1.txt", true);
		file.append("115");
		file.flush();
		file.close();
	}

}
