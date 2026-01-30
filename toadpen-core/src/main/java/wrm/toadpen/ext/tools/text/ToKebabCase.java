package wrm.toadpen.ext.tools.text;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.text.ToKebabCase")
public class ToKebabCase implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    context.replaceSelectedText(toKebabCase(text));
  }

  private String toKebabCase(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    // Replace underscores with hyphens
    String result = input.replace('_', '-');

    // Insert hyphens before uppercase letters (for camelCase/PascalCase)
    result = result.replaceAll("([a-z])([A-Z])", "$1-$2");
    result = result.replaceAll("([A-Z])([A-Z][a-z])", "$1-$2");

    // Replace spaces and multiple hyphens with single hyphen
    result = result.replaceAll("[\\s]+", "-");
    result = result.replaceAll("-+", "-");

    return result.toLowerCase();
  }

}
