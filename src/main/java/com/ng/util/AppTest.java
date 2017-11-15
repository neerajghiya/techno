

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.mbusa.mef.util.FileUtil;

public class AppTest {
	public static void main(String arg[]) throws IOException
	 {
		String fileContent = null;
		fileContent = FileUtil.readFile("D:\\Code\\BPT_Enhancement\\Test\\CSV\\ListLogs4.html");
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(fileContent);
		System.out.println("hi");
		List<String> list = new ArrayList<String>();
		while(m.find()) {
		//   System.out.println(m.group(0));
		  // System.out.println(m.group(1));
		   String link = m.group(0);
		   if(link.indexOf("mbhoisnapp851.usfdc.corpintra.net:10000/logview/downloadservlet?filename=") > 0){
			   list.add(link);
		   }
		}
		System.out.println("hi2");
		for (String string : list) {
			string = string.replaceAll("amp;", "");
			System.out.println("hi3" + string);
			
			int beginIndex = string.indexOf("filename=");
			int endIndex = string.indexOf("&");
			String fileName = string.substring(beginIndex + 9, endIndex);
			System.out.println("Filename = " + fileName);
			if(fileName.indexOf("_")>0){
				System.out.println("Filename with underscore = " + fileName);
				String destination = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\CostElementPROD1";
				URL url2 = new URL(string);
				File file = new File(destination + "\\" + fileName);
				FileUtils.copyURLToFile(url2, file);
			}
		}
		
		
			/*String url = "http://mbhoisnapp851.usfdc.corpintra.net:10000/logview/downloadservlet?filename=7US011000119_616010.xml&profile=AppSrv01&folder=eai/sapeaifin-cofico/bpt/data-store/xmls/US01";
			URL url2 = new URL(url);
			String destination = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\TestUS01";
			File file = new File(destination + "\\7US011000119_616010.xml");
			FileUtils.copyURLToFile(url2, file);*/
			System.out.println("Completed");
			/*String to = "--245.12";
			
			to = to.replaceAll("[-]","");
			
			to = "-" + to;
			
			System.out.println(to);*/
	        /*System.out.println( "Hello World!" );
	        String str = "100.00";
	        str = BPTUtil.removeQuotesAndComma(str);
	        Long val = new Long(str);
	    		System.out.println((val));*/
		
	    		
	    	/*if(0.0 == 0){
	    		System.out.println("hhehehehe");
	    	}*/
	    		
	    		/*if(val != 0 || val != 0){*/
	        
	       /* JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(CostCenterPlan.class);
				 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			 	   StringWriter sw = new StringWriter();
			 	   jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true );
			 	   jaxbMarshaller.marshal(custValueSegRecords, System.out); 
			 	   //Headers headers = getHeaders();
			 	  CostCenterPlan response = getResponse();
			 	   jaxbMarshaller.marshal(response, sw);
			 	   String result = sw.toString();
			 	   System.out.println(result);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
		
		//String cc = "123,456,789,234,567";
	//	List<String> ccList = new ArrayList<String>(Arrays.asList(cc.split(",")));
		/*for (String costcenter : ccList) {
			System.out.println("ccccc = " + costcenter);
			if(costcenter.equals("3456")){
				System.out.println("find");
			}
		}*/
		
		/*if(ccList.contains("456")){
			System.out.println("hhehehehheeh");
		}*/
	 	  
	    }

	private static CostCenterPlan getResponse() {
		CostCenterPlan ccp = new CostCenterPlan();
		CostCenter cc = new CostCenter();
		cc.setDescription("adasdf");
		cc.setEntered("aaaaaa");
		CostCenters ccs = new CostCenters();
		List<CostCenter> ccsList = new ArrayList<CostCenter>();
		ccsList.add(cc);
		ccs.setCostCenterList(ccsList);
		ccp.setCostCenters(ccs);
		ccp.setStatus("Yup");
		return ccp;
	}
}
