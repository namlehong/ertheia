package databuilder.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlPretty {

	public static String prettyFormat(String input) {
	    return XmlPretty.prettyFormat(input, 4);
	}

	public static String prettyFormat(String input, int indent) {
	    try
	    {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        // This statement works with JDK 6
	        transformerFactory.setAttribute("indent-number", indent);
	         
	        Transformer transformer = transformerFactory.newTransformer();
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

}
