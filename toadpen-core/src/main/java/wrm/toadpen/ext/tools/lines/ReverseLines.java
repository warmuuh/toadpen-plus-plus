package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.ReverseLines")
public class ReverseLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String[] lines = context.getSelectedText().split("\n");
    List<String> lineList = Arrays.asList(lines);
    Collections.reverse(lineList);
    context.replaceSelectedText(String.join("\n", lineList));
  }

}
