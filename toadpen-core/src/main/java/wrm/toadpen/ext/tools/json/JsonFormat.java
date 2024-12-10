package wrm.toadpen.ext.tools.json;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
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
    } else if (object instanceof Iterable) {
      buf.append("[\n");
      ((Iterable) object).forEach(o -> {
        indent(buf, indent + 1);
        serializeJson(o, buf, indent + 1);
        buf.append(",\n");
      });
      indent(buf, indent);
      buf.append("]");
    } else if (object instanceof Map) {
      buf.append("{\n");
      ((Map) object).forEach((k, v) -> {
        indent(buf, indent + 1);
        buf.append("\"").append(k).append("\": ");
        serializeJson(v, buf, indent + 1);
        buf.append(",\n");
      });
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
