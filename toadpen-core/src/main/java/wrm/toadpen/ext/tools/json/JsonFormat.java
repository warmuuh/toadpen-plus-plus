package wrm.toadpen.ext.tools.json;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;


@Singleton
@Named("tools.json.format")
public class JsonFormat implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Object parse = Json.parse(new StringReader(context.getSelectedText()));
    StringBuffer buf = new StringBuffer();
    serializeJson(parse, buf, 0);
    context.replaceSelectedText(buf.toString());
  }

  private void serializeJson(Object object, StringBuffer buf, int indent) {
    if (object instanceof String) {
      buf.append("\"").append(object).append("\"");
    } else if (object instanceof Number) {
      buf.append(object);
    } else if (object instanceof Boolean) {
      buf.append(object);
    } else if (object instanceof List list) {
      buf.append("[\n");
      for (int i = 0; i < list.size(); i++) {
        var o = list.get(i);
        indent(buf, indent + 1);
        serializeJson(o, buf, indent + 1);
        if (i < list.size() - 1) {
          buf.append(",\n");
        }
      }
      indent(buf, indent);
      buf.append("]");
    } else if (object instanceof Map map) {
      buf.append("{\n");
      LinkedList entries = new LinkedList<>(map.entrySet());
      for (int i = 0; i < entries.size(); i++) {
        Map.Entry entry = (Map.Entry) entries.get(i);
        indent(buf, indent + 1);
        buf.append("\"").append(entry.getKey()).append("\": ");
        serializeJson(entry.getValue(), buf, indent + 1);
        if (i < entries.size() - 1) {
          buf.append(",\n");
        }
      }

      indent(buf, indent);
      buf.append("}");
    }

  }

  private void indent(StringBuffer buf, int i) {
    for (int j = 0; j < i; j++) {
      buf.append("  ");
    }
  }

}
