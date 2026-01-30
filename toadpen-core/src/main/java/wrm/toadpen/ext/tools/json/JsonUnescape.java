package wrm.toadpen.ext.tools.json;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.json.unescape")
public class JsonUnescape implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    String unescaped = unescapeJson(text);
    context.replaceSelectedText(unescaped);
  }

  private String unescapeJson(String value) {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);

      if (c == '\\' && i + 1 < value.length()) {
        char next = value.charAt(i + 1);

        switch (next) {
          case '\\' -> {
            result.append('\\');
            i++;
          }
          case '"' -> {
            result.append('"');
            i++;
          }
          case 'n' -> {
            result.append('\n');
            i++;
          }
          case 'r' -> {
            result.append('\r');
            i++;
          }
          case 't' -> {
            result.append('\t');
            i++;
          }
          case 'b' -> {
            result.append('\b');
            i++;
          }
          case 'f' -> {
            result.append('\f');
            i++;
          }
          case '/' -> {
            result.append('/');
            i++;
          }
          case 'u' -> {
            // Unicode escape sequence uXXXX
            if (i + 5 < value.length()) {
              try {
                String hex = value.substring(i + 2, i + 6);
                int codePoint = Integer.parseInt(hex, 16);
                result.append((char) codePoint);
                i += 5;
              } catch (NumberFormatException e) {
                // Invalid unicode escape, keep as-is
                result.append(c);
              }
            } else {
              result.append(c);
            }
          }
          default -> result.append(c);
        }
      } else {
        result.append(c);
      }
    }

    return result.toString();
  }

}
