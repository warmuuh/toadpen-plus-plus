package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.NumberLines")
public class NumberLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    String[] lines = text.split("\n", -1);

    // Calculate width needed for line numbers
    int maxLineNumber = lines.length;
    int width = String.valueOf(maxLineNumber).length();

    StringBuilder result = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {
      String lineNumber = String.format("%" + width + "d", i + 1);
      result.append(lineNumber).append(" ").append(lines[i]);

      if (i < lines.length - 1) {
        result.append("\n");
      }
    }

    context.replaceSelectedText(result.toString());
  }

}
