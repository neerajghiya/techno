import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternMatching {

	public static void main(String[] args) {
		String str = "500.00";
		if(Pattern.matches("^\\d+(?:\\.\\d{0,2})?$", str)){// @todo amount decimal 2 places
			 System.out.println("True");
		}else{
			System.out.println("false");
		}
		
		removeSpecialCharacter();

	}
	
	private  static void removeSpecialCharacter(){
		String c= "hjdg$h&jk8^i0ssh6";
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(c);
        while(match.find())
        {
            String s= match.group();
        c=c.replaceAll("\\"+s, "");
        }
        System.out.println(c);
	}


}

