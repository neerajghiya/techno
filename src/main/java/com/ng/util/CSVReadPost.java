import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;



public class CSVReadPost {

	public static void main(String[] args) {
		String csvFilePath = "D:\\MbusaGit\\Code\\Other\\csv\\Employee.csv";
		try {
			List<Employee> employeeList = readCSVToEmployee(csvFilePath);
			ObjectMapper mapper = new ObjectMapper();
			String jsonString;
			for (Employee employee : employeeList) {
				jsonString = mapper.writeValueAsString(employee);
				System.out.println(jsonString);
				sendRestCall(jsonString);
			}
			
		} catch (IOException e) {
			System.out.println("Exception caught = " + e) ;
		}

	}

	private static List<Employee> readCSVToEmployee(String csvFilePath) throws IOException {
		String line = "";
		String cvsSplitBy = ",";
		BufferedReader br = null;
		File file = new File(csvFilePath);
		String[] csvValues;
		List<Employee> employeeList = new ArrayList<Employee>();
		Employee employee = null;
		
		if (file.exists()) {
			System.out.println("File exists");
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
                // use comma as separator
				csvValues = line.split(cvsSplitBy);
				employee = new Employee();
				employee.setfName(csvValues[0]);
				employee.setlName(csvValues[1]);
				employee.setAge(csvValues[2]);
				employee.setAge(csvValues[3]);
				employeeList.add(employee);
            }
			br.close();
		}
		
		return employeeList;
	}
	
	private static void sendRestCall(String data) {
		Boolean sentSuccessfully = Boolean.FALSE;
		System.out.println("Inside sendRestCall with data = " + data);
		try {
			URL url = new URL("http://demo4623930.mockable.io");//new URL(StarleadUtil.getRestLeadUpdateUrl("leadUpdate"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/xml");

			String input = data;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
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

}
