package bit.minisys.minicc.icgen;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public interface IMiniCCICGen {
	public void run(String iFile, String oFile) throws Exception;
}
