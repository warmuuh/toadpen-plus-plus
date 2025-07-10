package wrm.toadpen.ext.tools.xml;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;
import wrm.toadpen.ext.tools.json.JsonUtil;


@Singleton
@Named("tools.xml.format")
public class XmlFormat implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Source xmlInput = new StreamSource(new StringReader(context.getSelectedText()));
    StringWriter stringWriter = new StringWriter();
    StreamResult xmlOutput = new StreamResult(stringWriter);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute("indent-number", 2);
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    transformerFactory.setAttribute(OutputKeys.OMIT_XML_DECLARATION, "yes");
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(xmlInput, xmlOutput);
    context.replaceSelectedText(xmlOutput.getWriter().toString());
  }

}