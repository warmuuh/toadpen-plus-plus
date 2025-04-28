package wrm.toadpen.ext.tools.json;


import java.util.List;
import java.util.Map;

public class JsonUtil {

  public static void serializeJson(Object object, StringBuffer buf, int indent) {
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
      java.util.LinkedList entries = new java.util.LinkedList<>(map.entrySet());
      for (int i = 0; i < entries.size(); i++) {
        java.util.Map.Entry entry = (java.util.Map.Entry) entries.get(i);
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

  public static void indent(StringBuffer buf, int i) {
    for (int j = 0; j < i; j++) {
      buf.append("  ");
    }
  }
}