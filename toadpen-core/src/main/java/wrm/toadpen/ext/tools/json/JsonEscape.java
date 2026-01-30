package wrm.toadpen.ext.tools.json;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.json.escape")
public class JsonEscape implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    String escaped = escapeJson(text);
    context.replaceSelectedText(escaped);
  }

  private String escapeJson(String value) {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);

      switch (c) {
        case '\\' -> result.append("\\\\");
        case '"' -> result.append("\\\"");
        case '\n' -> result.append("\\n");
        case '\r' -> result.append("\\r");
        case '\t' -> result.append("\\t");
        case '\b' -> result.append("\\b");
        case '\f' -> result.append("\\f");
        default -> {
          // Escape control characters
          if (c < 0x20) {
            result.append(String.format("\\u%04x", (int) c));
          } else {
            result.append(c);
          }
        }
      }
    }

    return result.toString();
  }

}
