package wrm.toadpen.ext.tools.text;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.text.ToCamelCase")
public class ToCamelCase implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    context.replaceSelectedText(toCamelCase(text));
  }

  private String toCamelCase(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    // Split by non-alphanumeric characters, underscores, and hyphens
    String[] words = input.split("[\\s_\\-]+");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      if (word.isEmpty()) {
        continue;
      }

      if (i == 0) {
        result.append(word.toLowerCase());
      } else {
        result.append(Character.toUpperCase(word.charAt(0)));
        result.append(word.substring(1).toLowerCase());
      }
    }

    return result.toString();
  }

}
