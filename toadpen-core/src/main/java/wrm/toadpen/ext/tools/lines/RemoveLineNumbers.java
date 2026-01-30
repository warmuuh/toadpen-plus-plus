package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.RemoveLineNumbers")
public class RemoveLineNumbers implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    String[] lines = text.split("\n", -1);

    StringBuilder result = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      // Remove line numbers at the start of the line
      // Matches patterns like: "1 ", "  1 ", "001 ", "1. ", "1: ", "1| ", "1→"
      line = line.replaceFirst("^\\s*\\d+[\\s.:→|]\\s*", "");

      result.append(line);

      if (i < lines.length - 1) {
        result.append("\n");
      }
    }

    context.replaceSelectedText(result.toString());
  }

}
