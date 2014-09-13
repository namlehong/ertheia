package databuilder.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import databuilder.MainBuilder;

public class XmlPretty {

	public static String prettyFormat(String input, String dtd_format) {
	    return XmlPretty.prettyFormat(input, dtd_format, 4);
	}

	public static String prettyFormat(String input, String dtd_format, int indent) {
	    try
	    {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        // This statement works with JDK 6
	        transformerFactory.setAttribute("indent-number", indent);
	         
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd_format);
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    }
	    catch (Throwable e)
	    {
	        // You'll come here if you are using JDK 1.5
	        // you are getting an the following exeption
	        // java.lang.IllegalArgumentException: Not supported: indent-number
	        // Use this code (Set the output property in transformer.
	        try
	        {
	            Source xmlInput = new StreamSource(new StringReader(input));
	            StringWriter stringWriter = new StringWriter();
	            StreamResult xmlOutput = new StreamResult(stringWriter);
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
	            transformer.transform(xmlInput, xmlOutput);
	            return xmlOutput.getWriter().toString();
	        }
	        catch(Throwable t)
	        {
	            return input;
	        }
	    }
	}
	
	public static void writeToFile(String fileName, String xmlInput, String dtdFormat, String folderName){
		String xmlOut = XmlPretty.prettyFormat(xmlInput, dtdFormat);
		writeToFile(folderName+fileName+".xml", xmlOut);
	}
	
	public static void writeToFile(String filePath, String content){
		String fileFullPath = MainBuilder._datapack_path + filePath;
//		content = content.replaceAll("\n*\t* *<!-", "\t<!-");
//		System.out.println(content);
		try {
			File file = new File(fileFullPath);
			if (!file.exists()) {
				file.createNewFile();
			}
		
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter buffer = new BufferedWriter(fw);
			buffer.write(content);
			buffer.close();
			System.out.println("Write success on "+filePath);
		} catch (IOException e) {
			System.out.print("error on "+fileFullPath+" "+e);
		}
	}

}
