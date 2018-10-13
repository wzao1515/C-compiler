package bit.minisys.minicc.codegen;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

public interface IMiniCCCodeGen {
	public void run(String iFile, String oFile) throws IOException, TransformerConfigurationException, ParserConfigurationException, SAXException, DocumentException;
}
