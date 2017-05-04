import java.io.File;


public class FileUtil {

	public static void main(String[] args) {
		try{
		String failureFilePath = "/usr/appdata/share/eai/starLead/duplicatePushLead2EAI/failure/2017-03-21";
		//	String failureFilePath = "D:/fileTest";
		File file = new File(failureFilePath);
		File file1 = new File(failureFilePath + "/dd.txt");
		boolean created = file1.createNewFile();
		System.out.println(created);
		if (file.exists()) {

			System.out.println("File exists");

			File[] paths = file.listFiles();
			for (File path : paths) {
				System.out.println(path.getAbsolutePath());
				//path.delete();
				System.out.println("Success delete"); 
			}
		}
		}catch(Exception e){
			System.out.println("Exception Caught ==" + e);
		}
	}

}
