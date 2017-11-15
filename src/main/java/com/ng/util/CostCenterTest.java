package com.mbusa.bpt2.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.mbusa.mef.util.FileUtil;

public class CostCenterTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String fileContent = null;
		fileContent = FileUtil.readFile("D:\\Code\\BPT_Enhancement\\Test\\CSV\\ListLogs.html");
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
			if(fileName.length() == 12){
				System.out.println("CC Filename = " + fileName);
				String destination = "D:\\Code\\BPT_Enhancement\\Test\\CSV\\TestUS01CC03";
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

	}

}
