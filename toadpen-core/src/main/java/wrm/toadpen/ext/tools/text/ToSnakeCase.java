package wrm.toadpen.ext.tools.text;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.text.ToSnakeCase")
public class ToSnakeCase implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    context.replaceSelectedText(toSnakeCase(text));
  }

  private String toSnakeCase(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    // Replace hyphens with underscores
    String result = input.replace('-', '_');

    // Insert underscores before uppercase letters (for camelCase/PascalCase)
    result = result.replaceAll("([a-z])([A-Z])", "$1_$2");
    result = result.replaceAll("([A-Z])([A-Z][a-z])", "$1_$2");

    // Replace spaces and multiple underscores with single underscore
    result = result.replaceAll("[\\s]+", "_");
    result = result.replaceAll("_+", "_");

    return result.toLowerCase();
  }

}
