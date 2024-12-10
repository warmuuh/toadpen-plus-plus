package wrm.toadpen.ext.tools.time;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.time.TimestampToIso")
public class TimestampToIso implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    //java  1733827302246
    //other 1733827316
    String timestamp = context.getSelectedText();
    long epochMilli = Long.parseLong(timestamp);
    if (timestamp.length() == 10) {
      epochMilli *= 1000;
    }
    String formatted = java.time.Instant.ofEpochMilli(epochMilli).toString();
    context.replaceSelectedText(formatted);
  }

}
