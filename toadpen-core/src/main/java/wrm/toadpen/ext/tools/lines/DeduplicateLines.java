package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Arrays;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.DeduplicateLines")
public class DeduplicateLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String[] lines = context.getSelectedText().split("\n");
    java.util.SequencedSet<String> set = new java.util.LinkedHashSet<>(Arrays.asList(lines));
    context.replaceSelectedText(String.join("\n", set));
  }

}
