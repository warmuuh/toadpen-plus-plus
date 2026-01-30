package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.ShuffleLines")
public class ShuffleLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    String[] lines = text.split("\n", -1);
    List<String> lineList = Arrays.asList(lines);
    Collections.shuffle(lineList);
    context.replaceSelectedText(String.join("\n", lineList));
  }

}
