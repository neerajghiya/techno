import java.io.FileNotFoundException;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mbusa.mef.util.FileUtil;
import com.mbusa.mef.util.XmlUtil;

public class ChangeXML {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String xml = FileUtil.readFile("D:\\StarleadsResent\\updateLead\\2017-04-20\\StarLead_201704200001022780400_1492660866809.xml");
		System.out.println(xml);
		String finalInput = generateDocumentNumber(xml);
		finalInput = changeBusinessServiceName(finalInput, "onlineLead");
		finalInput = addElementToCurrentXml(finalInput, "ClientIP", "rttt");
		System.out.println(finalInput);

	}
	
	public static String addElementToCurrentXml(String xml, String tagName, String tagValue){
		String finalInput = "";
		Document doc = XmlUtil.parseAsXml(xml);
		Element root = doc.getDocumentElement();
		Node messageHeaderNode = XmlUtil.getXNode(root, "/Message/MessageHeader");
		Element nameElement = doc.createElement(tagName);
		messageHeaderNode.appendChild(nameElement);
		XmlUtil.setXNodeValue(root, "/Message/MessageHeader/" + tagName, tagValue);
		finalInput = XmlUtil.serializeAsString(doc.getDocumentElement(),true);
		return finalInput;
	}
	
	public static String changeBusinessServiceName(String xml, String businessServiceName) {
		Document doc = XmlUtil.parseAsXml(xml);
		Element root = doc.getDocumentElement();
		XmlUtil.setXNodeValue(root, "/Message/MessageHeader/BusinessService", businessServiceName);
		
		String finalInput = XmlUtil.serializeAsString(doc.getDocumentElement(),true);
		
		return finalInput;
	}
	
	public static String generateDocumentNumber(String xml){
		String finalInput = "";
		
		long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation
        
        long midSeed = (long) (timeSeed * randSeed); 

        String s = midSeed + "";
        String subStr = s.substring(0, 9);

        Integer finalSeed = new Integer(subStr);    // integer value
        
        Document doc = XmlUtil.parseAsXml(xml);
		Element root = doc.getDocumentElement();
		
		Node messageHeaderNode = XmlUtil.getXNode(root, "/Message/BusinessData/ProcessSalesLeads/ProcessSalesLead/DataArea/SalesLead/Header");
		Element nameElement = doc.createElement("DocumentId");
		messageHeaderNode.appendChild(nameElement);
		XmlUtil.setXNodeValue(root, "/Message/BusinessData/ProcessSalesLeads/ProcessSalesLead/DataArea/SalesLead/Header/DocumentId", finalSeed.toString());
		finalInput = XmlUtil.serializeAsString(doc.getDocumentElement(),true);
		return finalInput;
	}

}
