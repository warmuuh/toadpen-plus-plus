package wrm.toadpen.ext.tools.json;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.json.minify")
public class JsonMinify implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Object parse = Json.parse(new StringReader(context.getSelectedText()));
    StringBuffer buf = new StringBuffer();
    serializeJsonMinified(parse, buf);
    context.replaceSelectedText(buf.toString());
  }

  private void serializeJsonMinified(Object object, StringBuffer buf) {
    if (object instanceof String) {
      buf.append("\"").append(object).append("\"");
    } else if (object instanceof Number) {
      buf.append(object);
    } else if (object instanceof Boolean) {
      buf.append(object);
    } else if (object == null) {
      buf.append("null");
    } else if (object instanceof List list) {
      buf.append("[");
      for (int i = 0; i < list.size(); i++) {
        serializeJsonMinified(list.get(i), buf);
        if (i < list.size() - 1) {
          buf.append(",");
        }
      }
      buf.append("]");
    } else if (object instanceof Map map) {
      buf.append("{");
      java.util.LinkedList entries = new java.util.LinkedList<>(map.entrySet());
      for (int i = 0; i < entries.size(); i++) {
        java.util.Map.Entry entry = (java.util.Map.Entry) entries.get(i);
        buf.append("\"").append(entry.getKey()).append("\":");
        serializeJsonMinified(entry.getValue(), buf);
        if (i < entries.size() - 1) {
          buf.append(",");
        }
      }
      buf.append("}");
    }
  }

}
