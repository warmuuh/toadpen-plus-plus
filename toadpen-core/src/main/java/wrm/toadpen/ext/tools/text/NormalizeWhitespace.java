package wrm.toadpen.ext.tools.text;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.text.NormalizeWhitespace")
public class NormalizeWhitespace implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();

    // Replace multiple whitespace characters (spaces, tabs, newlines) with a single space
    String result = text.replaceAll("\\s+", " ");

    // Trim leading and trailing whitespace
    result = result.trim();

    context.replaceSelectedText(result);
  }

}
