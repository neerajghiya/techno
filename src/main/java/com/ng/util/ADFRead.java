import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mbusa.mef.util.FileUtil;
import com.mbusa.mef.util.XmlUtil;


public class ADFRead {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String xml = FileUtil.readFile("D:\\StarleadsResent\\updateLead\\2017-0503\\onlineLead.xml");
		//System.out.println(xml);
		String xPath = "/Message/BusinessData/DestinationSoftwareCode";
		String destinationSoftwareCode = getElementValue(xml, xPath);
		System.out.println("destinationSoftwareCode =" + destinationSoftwareCode);
		xPath = "/Message/BusinessData/adf/prospect/id";
		String documentId = getElementValue(xml, xPath);
		System.out.println("documentId =" + documentId);
		xPath = "/Message/BusinessData/adf/prospect/vendor/id";
		String dealerCode = getElementValue(xml, xPath);
		System.out.println("dealerCode =" + dealerCode);

	}
	
	public static String getElementValue(String xml, String xPath){
		Document doc = XmlUtil.parseAsXml(xml);
		Element root = doc.getDocumentElement();
		String nodeValue = getNodeValue(root, xPath);
		
		return nodeValue;
	}
	
	private static String getNodeValue(Element element, String xQuery) {
		//LoggingServices.debug(StarleadUtil.class, "Inside getNodeValue");
		String nodeValue = "";
		try {
			Node elementNode = XPathAPI.selectSingleNode(element, xQuery);
			if (elementNode != null) {
				Node node = elementNode.getLastChild();
				if (node != null) {
					nodeValue = node.getNodeValue();
					//LoggingServices.debug(elementNode + " " + nodeValue);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//LoggingServices.debug(StarleadUtil.class, "Outside getNodeValue");
		return nodeValue;
	}

}
