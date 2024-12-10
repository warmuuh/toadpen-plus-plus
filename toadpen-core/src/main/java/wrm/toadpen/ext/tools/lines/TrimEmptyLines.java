package wrm.toadpen.ext.tools.lines;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.lines.TrimEmptyLines")
public class TrimEmptyLines implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String[] lines = context.getSelectedText().split("\n");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (StringUtils.isNotBlank(line)) {
        sb.append(line);
        if (i < lines.length - 1) {
          sb.append("\n");
        }
      }
    }
    context.replaceSelectedText(sb.toString());
  }

}
