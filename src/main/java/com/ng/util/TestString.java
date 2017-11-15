import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TestString {

	public static void main(String[] args) {
		//checkContains();
		//checkListContains();
		//addLeadingZero("02", "0", 2);
		System.out.println(getAmount("250", false, false));

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
	
	private static void addLeadingZero(String number,String pad, int len){
		  while (number.length() < len){
			  number = pad + number;
		  }
		  
		  System.out.println(number);
		
		
	}
	
	public static int getAmount(String amountStr, boolean isAddition, boolean isEfficiency){
		int amount = 0;
		if(StringUtils.isNotEmpty(amountStr)){
			amountStr = removeQuotesAndCommaDB(amountStr);
			boolean isNeg = false;
			while(amountStr.startsWith("-") || (amountStr.startsWith("0") && amountStr.length()>1)){
				if(amountStr.startsWith("-")){
					isNeg = true;
				}
				amountStr = amountStr.substring(1);
			}
			if(StringUtils.isNotEmpty(amountStr)){
				amount = new Double(amountStr).intValue();
			}else{
				return amount;
			}
			if(!isAddition){
				if(isNeg || isEfficiency){
					amount = -amount;
				}
			}
		}
		return amount;
	}
	
	public static String removeQuotesAndCommaDB(String value){
		if(StringUtils.isNotEmpty(value)){
			value = value.replaceAll(",","");
			value = value.replaceAll("\"","");
		}else{
			value="0";
		}
		return value;
	}

}
