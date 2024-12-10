package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.SortLines")
public class SortLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String[] lines = context.getSelectedText().split("\n");
    java.util.Arrays.sort(lines);
    context.replaceSelectedText(String.join("\n", lines));
  }

}
