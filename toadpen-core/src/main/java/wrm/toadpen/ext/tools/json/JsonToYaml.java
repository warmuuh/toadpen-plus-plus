package wrm.toadpen.ext.tools.json;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;
import wrm.toadpen.core.tools.ToolManager.TextTool;

@Singleton
@Named("tools.json.toYaml")
public class JsonToYaml implements TextTool {
  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Object parse = Json.parse(new StringReader(context.getSelectedText()));
    StringBuffer buf = new StringBuffer();
    serializeYaml(parse, buf, 0);
    context.replaceSelectedText(buf.toString());
  }

  private void serializeYaml(Object object, StringBuffer buf, int indent) {
    if (object instanceof String) {
      buf.append(object);
    } else if (object instanceof Number) {
      buf.append(object);
    } else if (object instanceof Boolean) {
      buf.append(object);
    } else if (object instanceof java.util.List list) {
      buf.append("\n");
      for (int i = 0; i < list.size(); i++) {
        var o = list.get(i);
        indent(buf, indent + 1);
        buf.append("- ");
        serializeYaml(o, buf, indent + 1);
        if (i < list.size() - 1) {
          buf.append("\n");
        }
      }
      indent(buf, indent);
    } else if (object instanceof java.util.Map map) {
      buf.append("\n");
      java.util.LinkedList entries = new java.util.LinkedList<>(map.entrySet());
      for (int i = 0; i < entries.size(); i++) {
        java.util.Map.Entry entry = (java.util.Map.Entry) entries.get(i);
        indent(buf, indent + 1);
        buf.append(entry.getKey()).append(": ");
        serializeYaml(entry.getValue(), buf, indent + 1);
        if (i < entries.size() - 1) {
          buf.append("\n");
        }
      }

      indent(buf, indent);
    }

  }

  private void indent(StringBuffer buf, int i) {
    for (int j = 0; j < i; j++) {
      buf.append("  ");
    }
  }

}
