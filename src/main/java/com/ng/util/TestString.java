import java.util.ArrayList;
import java.util.List;

public class TestString {

	public static void main(String[] args) {
		checkContains();
		checkListContains();

	}
	
	private static void checkContains(){
		System.out.println("Testing checkContains()");
		String str = "https://mbportal-qa.e.corpintra.net/pages/MyPage?site=http%3A%2F%2Fmbhobgnapp801.americas.bg.corpintra.net%3A9080%2FaccrualTool%2Findex.jsp";
		
		System.out.println(str.contains("corpintra"));
		System.out.println(str.contains("Software"));
		
	}
	
	private static void checkListContains(){
		System.out.println("Testing checkListContains()");
		List<String> strList = new ArrayList<String>();
		strList.add("Neeeraj");
		strList.add("google");
		strList.add("bye/");
		
		System.out.println(strList.contains("bye"));
		System.out.println(strList.contains("neeraj"));
		System.out.println(strList.contains("bye/"));
		System.out.println(strList.contains("Neeeraj"));
		
	}

}
