package com.mbusa.bpt2.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mbusa.bpt2.util.BPTUtil;
import com.mbusa.mef.util.FileUtil;
import com.mbusa.mef.util.XmlUtil;

public class CostElementFileCheck {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, TransformerException {
		String fileContent = null;
		fileContent = FileUtil.readFile("D:\\Code\\BPT_Enhancement\\Test\\CSV\\PostedCCFile.csv");
		
		String[] cc = fileContent.split(",");
		System.out.println("hi");
		int i=0;
		
		Document doc = null;
		for (String costCenter : cc) {
			
			String ccPath = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\TestUS01CC\\"+costCenter.trim()+".xml";
			String ccContent = FileUtil.readFile(ccPath);
			ccContent = BPTUtil.removeChar(ccContent, '\t');
			ccContent = BPTUtil.removeChar(ccContent, '\n');
			ccContent = BPTUtil.removeChar(ccContent, '\r');
			
			doc = XmlUtil.parseAsXml(ccContent);
			
			Element element = null;
			element = doc.getDocumentElement();
			Iterator ioIterator = XmlUtil.getXNodeIterator(element, "//InternalOrder");
			while ( ioIterator != null && ioIterator.hasNext() ) 
			{
				Element ioElement = (Element) ioIterator.next();
				String io = (XmlUtil.getXNodeValue( ioElement , "OrderNumber"));
				String filePath = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\TestUS01IO\\"+io+".xml";
				String ioFileContent = FileUtil.readFile(filePath);
				ioFileContent = BPTUtil.removeChar(ioFileContent, '\t');
				ioFileContent = BPTUtil.removeChar(ioFileContent, '\n');
				ioFileContent = BPTUtil.removeChar(ioFileContent, '\r');
				doc = XmlUtil.parseAsXml(ioFileContent);
				element = doc.getDocumentElement();
				Iterator ceIterator = XmlUtil.getXNodeIterator(element, "//CostElementSum");
				while ( ceIterator != null && ceIterator.hasNext() ) 
				{
					Element ceElement = (Element) ceIterator.next();
					String ce = (XmlUtil.getXNodeValue( ceElement , "CostElement"));
					String cyPlan = (XmlUtil.getXNodeValue( ceElement , "CYPlanTotal"));
					cyPlan = BPTUtil.removeQuotesAndComma(cyPlan);
					Integer cy = Integer.parseInt(cyPlan);
					//System.out.println(fileName);
					if(cy>0){
						String cePath = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\CostElement1";
						String cefile = cePath + File.separator + io + "_" + ce + ".xml";
						if (!FileUtil.existsFile(cefile)){
							System.out.println("File Not Exist = " + io + "_" + ce + ".xml");
							i++;
						}
					}
				}
			}
		
			
		}
		System.out.println("Total Files Not found = " + i);
	}
}
