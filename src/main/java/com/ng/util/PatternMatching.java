import java.util.regex.Pattern;


public class PatternMatching {

	public static void main(String[] args) {
		String str = "500.00";
		if(Pattern.matches("^\\d+(?:\\.\\d{0,2})?$", str)){// @todo amount decimal 2 places
			 System.out.println("True");
		}else{
			System.out.println("false");
		}
	

	}

}
